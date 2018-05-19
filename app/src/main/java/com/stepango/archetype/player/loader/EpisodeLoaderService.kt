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
import com.stepango.archetype.action.IntentMaker
import com.stepango.archetype.action.argsOf
import com.stepango.archetype.action.intent
import com.stepango.archetype.logger.logger
import com.stepango.archetype.player.data.db.model.EpisodeDownloadState
import com.stepango.archetype.player.data.db.model.EpisodesModel
import com.stepango.archetype.player.di.injector
import com.stepango.archetype.player.di.lazyInject
import com.stepango.archetype.player.episodeId
import com.stepango.archetype.rx.CompositeDisposableHolder
import com.stepango.archetype.rx.filterNonEmpty
import com.stepango.archetype.rx.subscribeBy
import com.stepango.archetype.util.getFileName
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.BufferedSink
import okio.BufferedSource
import okio.Okio
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

const val CANCEL_DOWNLOAD_ACTION = "cancel_action"

class EpisodeLoaderService : Service() {

    companion object {
        fun intent(episodeId: Long, intentMaker: IntentMaker, context: Context) = intentMaker.intent<EpisodeLoaderService>(context, argsOf { episodeId { episodeId } })
    }

    val episodeLoader by lazy { EpisodeLoader(this, injector.compositeDisposableHolder()) }

    val cancelReceiver = object : BroadcastReceiver() {
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

class EpisodeLoader(val service: Service, compositeDisposableHolder: CompositeDisposableHolder) :
        ProgressUpdateListener,
        CompositeDisposableHolder by compositeDisposableHolder {

    val toaster by lazyInject { toaster() }
    val episodes by lazy { Episodes() }

    val queueSubject: PublishSubject<Long> = PublishSubject.create<Long>()
    val notificationHelper = NotificationHelper(service, hashCode())
    val loader = ProgressOkLoader(this)
    val waitStack = ArrayList<Long>()

    val loadScheduler: Scheduler = Schedulers.single()

    init {
        queueSubject
                .timeout(10, TimeUnit.MINUTES)
                .observeOn(loadScheduler)
                .subscribeBy(
                        onNext = { load(it) },
                        onError = { stopLoading() }
                )
                .bind()
    }

    fun startForeground() {
        service.startForeground(hashCode(), notificationHelper.getNotification())
    }

    fun stopForeground() {
        service.stopForeground(true)
    }

    fun clearWaitStack(): Completable {
        return Observable.fromIterable(waitStack)
                .flatMapCompletable { episodes.updateState(it, EpisodeDownloadState.DOWNLOAD).toCompletable() }
    }

    fun queue(id: Long) {
        episodes.updateState(id, EpisodeDownloadState.WAIT)
                .doOnSuccess { waitStack.add(it.id) }
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                        onSuccess = { queueSubject.onNext(it.id) },
                        onError = { toaster.showToast("error on queue for episode $id") }
                ).bind()
    }

    fun load(id: Long) {
        logger.d("start load for $id")
        episodes.getById(id)
                .flatMap { loadEpisode(it) }
                .subscribeOn(loadScheduler)
                .subscribeBy(
                        onSuccess = { logger.d("loading done for $id") },
                        onError = { toaster.showToast("error on loading $id") }
                ).bind()
    }

    fun loadEpisode(episode: EpisodesModel): Single<EpisodesModel> {
        return prepareEpisode(episode)
                .flatMap { startTask(it) }
                .doAfterTerminate { stopForeground() }
                .doOnError { handleLoadErrorFor(episode.id) }
    }

    fun prepareEpisode(episode: EpisodesModel): Single<EpisodesModel> {
        return episodes.updateState(episode, EpisodeDownloadState.CANCEL)
                .doOnSuccess { waitStack.remove(it.id) }
                .doOnSuccess { startForeground() }
    }

    fun handleLoadErrorFor(id: Long) {
        episodes.updateState(id, EpisodeDownloadState.RETRY)
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                        onError = { toaster.showToast("can't set RETRY for episode $id") }
                ).bind()
    }

    fun startTask(episode: EpisodesModel): Single<EpisodesModel> {
        notificationHelper.text(episode.name)
        val file = File(service.filesDir, getFileName(episode.audioUrl))
        return EpisodeDownloadTask(
                loader.client,
                episode,
                file)
                .execute()
                .doOnError { file.delete() }
                .flatMap { episodes.updateFile(episode, it) }
    }

    override fun onProgressUpdate(current: Long, total: Long, done: Boolean) {
        notificationHelper.progress(current, total)
    }

    fun stopLoading() {
        clearWaitStack()
                .doAfterTerminate { resetCompositeDisposable() }
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                        onComplete = {
                            service.stopForeground(true)
                            service.stopSelf()
                        }
                ).bind()
    }
}

class Episodes {
    val repo by lazyInject { episodesRepo() }

    fun updateState(id: Long, newState: EpisodeDownloadState): Single<EpisodesModel> = getById(id).flatMap { updateState(it, newState) }

    fun updateState(episode: EpisodesModel, newState: EpisodeDownloadState): Single<EpisodesModel> = repo.save(episode.id, episode.copy(state = newState))

    fun updateFile(episode: EpisodesModel, file: File): Single<EpisodesModel> = repo.save(episode.id, episode.copy(file = file.absolutePath))

    fun getById(id: Long): Single<EpisodesModel> = repo.observe(id)
            .filterNonEmpty()
            .firstOrError()
}

class EpisodeDownloadTask(
        val client: OkHttpClient,
        val episode: EpisodesModel,
        val destination: File
) {

    fun execute(): Single<File> = Single.fromCallable<File> {
        val request: Request = createRequest(episode.audioUrl)
        val response: Response = client.newCall(request).execute()

        if (!response.isSuccessful) throw IOException("Unexpected code $response")
        saveToFile(response.body().source())
        return@fromCallable destination
    }

    fun createRequest(url: String): Request = Request.Builder().url(url).build()
    fun saveToFile(src: BufferedSource) {
        val sink: BufferedSink = Okio.buffer(Okio.sink(destination))
        sink.writeAll(src)
        sink.close()
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
        progress = if (total > 0) ((100 * current) / total).toInt() else 100
    }

    fun getNotification(): Notification = builder.build()
}

