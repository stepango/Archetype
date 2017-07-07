package com.stepango.archetype.player.loader

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import com.stepango.archetype.R
import com.stepango.archetype.action.Args
import com.stepango.archetype.logger.logger
import com.stepango.archetype.player.data.db.model.EpisodeDownloadState
import com.stepango.archetype.player.data.db.model.EpisodesModel
import com.stepango.archetype.player.di.lazyInject
import com.stepango.archetype.player.episodeId
import com.stepango.archetype.rx.CompositeDisposableComponent
import com.stepango.archetype.rx.CompositeDisposableComponentImpl
import com.stepango.archetype.util.getFileName
import com.stepango.archetype.viewmodel.onCompleteStub
import com.stepango.archetype.viewmodel.onErrorStub
import com.stepango.archetype.viewmodel.onNextStub
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.PublishSubject
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.BufferedSink
import okio.Okio
import java.io.File
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Wild, 01.07.2017.
 */

const val CANCEL_DOWNLOAD_ACTION = "cancel_action"

class EpisodeLoaderService : Service() {

    val episodeLoader by lazy { EpisodeLoader(this) }

    val cancelReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            episodeLoader.stopLoading()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        registerReceiver(cancelReceiver, IntentFilter(CANCEL_DOWNLOAD_ACTION))
    }

    override fun onDestroy() {
        unregisterReceiver(cancelReceiver)
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val args: Args = intent.extras
        episodeLoader.queue(args.episodeId())
        return START_STICKY
    }
}

class EpisodeLoader(val service: Service) :
        ProgressUpdateListener,
        CompositeDisposableComponent by CompositeDisposableComponentImpl() {

    fun <T : Any> Observable<T>.bindSubscribe(
            scheduler: Scheduler = io(),
            onNext: (T) -> Unit = onNextStub,
            onError: (Throwable) -> Unit = onErrorStub,
            onComplete: () -> Unit = onCompleteStub
    ) = subscribeOn(scheduler).subscribe(onNext, onError, onComplete).bind()

    fun <T : Any> Single<T>.bindSubscribe(
            scheduler: Scheduler = io(),
            onSuccess: (T) -> Unit = onNextStub,
            onError: (Throwable) -> Unit = onErrorStub
    ) = subscribeOn(scheduler).subscribe(onSuccess, onError).bind()

    fun Completable.bindSubscribe(
            scheduler: Scheduler = io(),
            onError: (Throwable) -> Unit = onErrorStub,
            onComplete: () -> Unit = onCompleteStub
    ) = subscribeOn(scheduler).subscribe(onComplete, onError).bind()

    val repo by lazyInject { episodesRepo() }
    val toaster by lazyInject { toaster() }

    val queueSubject: PublishSubject<Long> = PublishSubject.create<Long>()
    val notificationHelper = NotificationHelper(service, hashCode())
    val loader = ProgressOkLoader(this)
    val waitStack = ArrayList<Long>()

    val loadScheduler: Scheduler = Schedulers.from(Executors.newSingleThreadExecutor())

    init {
        queueSubject
                .timeout(10, TimeUnit.MINUTES)
                .observeOn(loadScheduler)
                .bindSubscribe(
                        onNext = { load(it) },
                        onError = { stopLoading() }
                )
    }

    fun startForeground() {
        service.startForeground(hashCode(), notificationHelper.getNotification())
    }

    fun stopForeground() {
        service.stopForeground(true)
    }

    fun clearWaitStack(): Completable {
        return Observable.fromIterable(waitStack)
                .flatMapCompletable { updateEpisodeState(it, EpisodeDownloadState.DOWNLOAD).toCompletable() }
    }

    fun queue(id: Long) {
        updateEpisodeState(id, EpisodeDownloadState.WAIT)
                .doOnSuccess { waitStack.add(it.id) }
                .bindSubscribe(
                        onSuccess = { queueSubject.onNext(it.id) },
                        onError = { toaster.showToast("error on queue for episode $id") }
                )
    }

    fun getEpisodeById(id: Long): Single<EpisodesModel>
        = repo.observe(id)
            .take(1)
            .map { it.get() }
            .firstOrError()

    fun load(id: Long) {
        logger.d("start load for $id")
        getEpisodeById(id)
                .doOnSuccess { waitStack.remove(it.id) }
                .doOnSuccess { startForeground() }
                .flatMap { updateEpisodeState(it, EpisodeDownloadState.CANCEL) }
                .flatMap { startTask(it) }
                .doAfterTerminate { stopForeground() }
                .doOnError { handleLoadErrorFor(id) }
                .bindSubscribe (
                        scheduler = loadScheduler,
                        onSuccess = { logger.d("loading done for $id") },
                        onError = { toaster.showToast("error on loading $id") }
                )
    }

    fun handleLoadErrorFor(id: Long) {
        updateEpisodeState(id, EpisodeDownloadState.RETRY)
                .bindSubscribe (
                        onError = { toaster.showToast("can't set RETRY for episode $id") }
                )
    }

    fun updateEpisodeState(episode: EpisodesModel, newState: EpisodeDownloadState): Single<EpisodesModel>
        = repo.save(episode.id, episode.copy(state = newState))

    fun updateEpisodeState(id: Long, newState: EpisodeDownloadState): Single<EpisodesModel>
        = repo.observe(id).take(1)
                .flatMapSingle { updateEpisodeState(it.get(), newState) }
                .firstOrError()

    fun updateEpisodeFile(episode: EpisodesModel, file: File): Single<EpisodesModel>
        = repo.save(episode.id, episode.copy(file = file.absolutePath))


    fun startTask(episode: EpisodesModel): Single<EpisodesModel> {
        notificationHelper.text(episode.name)
        val file = File(service.filesDir, getFileName(episode.audioUrl))
        return EpisodeDownloadTask(
                    loader.client,
                    episode,
                    file)
                .execute()
                .doOnError { file.delete() }
                .flatMap { updateEpisodeFile(episode, it) }
    }

    override fun onProgressUpdate(current: Long, total: Long, done: Boolean) {
        notificationHelper.progress(current, total)
    }

    fun stopLoading() {
        clearWaitStack()
                .doAfterTerminate { resetCompositeDisposable() }
                .bindSubscribe(
                        onComplete = {
                            service.stopForeground(true)
                            service.stopSelf()
                        }
                )
    }
}

class EpisodeDownloadTask(
        val client: OkHttpClient,
        val episode: EpisodesModel,
        val destination: File
) {

    fun execute(): Single<File> = Single.fromCallable<File> {
        val request = Request.Builder()
                .url(episode.audioUrl)
                .build()

        val response: Response = client.newCall(request).execute()
        if (!response.isSuccessful) throw IOException("Unexpected code $response")
        val sink: BufferedSink = Okio.buffer(Okio.sink(destination))
        sink.writeAll(response.body().source())
        sink.close()
        return@fromCallable destination
    }
}

class NotificationHelper(context: Context, val id: Int) {

    val REQUEST_CANCEL = 0
    val mgr = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
    val builder: NotificationCompat.Builder = NotificationCompat.Builder(context)
            .setContentTitle(context.getString(R.string.app_name))
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .addAction(
                    android.R.drawable.ic_menu_close_clear_cancel,
                    context.getString(android.R.string.cancel),
                    PendingIntent.getBroadcast(
                            context,
                            REQUEST_CANCEL,
                            Intent(CANCEL_DOWNLOAD_ACTION),
                            PendingIntent.FLAG_ONE_SHOT
                    )
            )
    var progress: Int = 0
        set(value) {
            if (field != value) {
                field = value
                builder.setProgress(100, field, false)
                mgr.notify(id, builder.build())
            }
        }

    fun text(value: String) {
        builder.setContentText(value)
    }

    fun progress(current: Long, total: Long) {
        progress = if (total > 0) ((100 * current)/total).toInt() else 100
    }

    fun getNotification(): Notification = builder.build()
}

