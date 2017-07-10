package com.stepango.archetype.player.loader

import android.content.Context
import com.stepango.archetype.action.*
import com.stepango.archetype.logger.logger
import com.stepango.archetype.player.data.db.model.EpisodesModel
import com.stepango.archetype.player.di.Injector
import com.stepango.archetype.player.di.lazyInject
import com.stepango.archetype.util.getFileName
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers.io
import java.io.File

/**
 * Wild, 02.07.2017.
 */

class RefreshDownloadedFilesAction : ContextAction {

    val episodesRepo by lazyInject { episodesRepo() }

    fun getAllEpisodes(): Observable<EpisodesModel>
            = episodesRepo.observeAll()
            .take(1)
            .flatMapIterable { it }

    fun checkEpisodeFile(episode: EpisodesModel, file: File): Completable {
        if (!file.exists())
            return Completable.complete()

        return episodesRepo
                .save(episode.id, episode.copy(file = file.absolutePath))
                .toCompletable()
    }

    override fun invoke(context: Context, args: Args): Completable {
        return getAllEpisodes()
                .subscribeOn(io())
                .flatMapCompletable {
                    val file = File(context.filesDir, getFileName(it.audioUrl))
                    return@flatMapCompletable checkEpisodeFile(it, file)
                }
    }
}

class DownloadEpisodeAction : IntentAction, IntentMaker by Injector().intentMaker() {

    override fun invoke(context: Context, args: Args): Completable
            = startService<EpisodeLoaderService>(context, args)

}

class CancelDownloadEpisodeAction : IntentAction, IntentMaker by Injector().intentMaker() {

    override fun invoke(context: Context, args: Args): Completable
            = startBroadcast(context, CANCEL_DOWNLOAD_ACTION)

}