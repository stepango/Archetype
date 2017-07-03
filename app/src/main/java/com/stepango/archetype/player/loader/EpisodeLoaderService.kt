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
import com.stepango.archetype.util.getFileName
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.BufferedSink
import okio.Okio
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Wild, 01.07.2017.
 */

const val CANCEL_DOWNLOAD_ACTION = "cancel_action"
private fun log(msg: String) {
    println(Thread.currentThread().name + " | " + msg)
}

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
        super.onDestroy()
        unregisterReceiver(cancelReceiver)
        episodeLoader.stopLoading()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val args: Args = intent.extras
        episodeLoader.queue(args.episodeId())
        return START_STICKY
    }
}

class EpisodeLoader(val service: Service) : ProgressUpdateListener {

    val repo by lazyInject { episodesRepo() }

    val queueSubject = PublishSubject.create<Long>()!!
    val notificationHelper = NotificationHelper(service, hashCode())
    val loader = ProgressOkLoader(this)
    val waitStack = ArrayList<Long>()
    val dispatcher = queueSubject
            .timeout(10, TimeUnit.MINUTES)
            .observeOn(Schedulers.io())
            .doOnError { log("error $it") }
            .subscribe(
                    { load(it) },
                    { stopLoading() }
            )!!

    fun startForeground() {
        service.startForeground(hashCode(), notificationHelper.getNotification())
    }

    fun stopForeground() {
        service.stopForeground(true)
    }

    fun clearWaitStack() {
        Observable
                .fromIterable(waitStack)
                .subscribe(
                        { updateEpisodeState(it, EpisodeDownloadState.DOWNLOAD) },
                        { logger.e("error $it") }
                )
    }

    fun queue(id: Long) {
        log("start queue for $id")
        Observable.just(queueSubject)
                .doOnNext { log("queue for $id") }
                .doOnNext { updateEpisodeState(id, EpisodeDownloadState.WAIT) }
                .doOnNext { waitStack.add(id) }
                .observeOn(Schedulers.newThread())
                .subscribe { it.onNext(id) }
    }

    fun load(id: Long) {
        log("load: $id")
        repo.get(id)
                .doOnSuccess { startForeground() }
                .doOnSuccess { updateEpisodeState(it, EpisodeDownloadState.CANCEL) }
                .flatMap { startTask(it) }
                .doAfterTerminate { stopForeground() }
                .subscribe(
                        {  },
                        {log("error with: $it")}
                )
    }

    fun updateEpisodeState(episode: EpisodesModel, newState: EpisodeDownloadState) {
        episode.state = newState
        repo.save(episode.id, episode)
                .subscribe()
    }

    fun updateEpisodeState(id: Long, newState: EpisodeDownloadState) {
        repo.get(id).subscribe( { updateEpisodeState(it, newState) }, { logger.e("error: $it") } )
    }

    fun updateEpisodeFile(episode: EpisodesModel, file: File) {
        episode.file = file.absolutePath
        repo.save(episode.id, episode)
                .subscribe()
    }

    fun startTask(episode: EpisodesModel): Single<File> {
        notificationHelper.text(episode.name)
        val file = File(service.filesDir, getFileName(episode.audioUrl))
        return EpisodeDownloadTask(
                    loader.client,
                    episode,
                    file)
                .execute()
                .doOnError { file.delete() }
                .doOnError { updateEpisodeState(episode, EpisodeDownloadState.RETRY) }
                .doOnSuccess { updateEpisodeFile(episode, it) }
                .doOnSuccess { waitStack.remove(episode.id) }
    }

    override fun onProgressUpdate(current: Long, total: Long, done: Boolean) {
        notificationHelper.progress(current, total)
    }

    fun stopLoading() {
        clearWaitStack()
        dispatcher.dispose()
        service.stopForeground(true)
        service.stopSelf()
    }
}

class EpisodeDownloadTask(
        val client: OkHttpClient,
        val episode: EpisodesModel,
        val destination: File
) {

    fun execute() = Single.fromCallable<File> {
        val request = Request.Builder()
                .url(episode.audioUrl)
                .build()

        val response: Response = client.newCall(request).execute()
        if (!response.isSuccessful) throw IOException("Unexpected code $response")
        val sink: BufferedSink = Okio.buffer(Okio.sink(destination))
        sink.writeAll(response.body().source())
        sink.close()
        return@fromCallable destination
    }!!
}

class NotificationHelper(context: Context, val id: Int) {

    val REQUEST_CANCEL = 0
    val mgr = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
    val builder = NotificationCompat.Builder(context)
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
            )!!
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

