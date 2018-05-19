package com.stepango.archetype.player.loader

import android.content.Context
import com.stepango.archetype.action.ContextAction
import com.stepango.archetype.action.IntentAction
import com.stepango.archetype.action.IntentMaker
import com.stepango.archetype.action.startBroadcast
import com.stepango.archetype.action.startService
import com.stepango.archetype.player.data.db.model.EpisodesModel
import com.stepango.archetype.player.di.Injector
import com.stepango.archetype.player.di.lazyInject
import com.stepango.archetype.util.getFileName
import io.reactivex.Completable
import io.reactivex.Observable
import java.io.File

/**
 * Wild, 02.07.2017.
 */

interface RefreshDownloadedAction : ContextAction<Unit>

class RefreshDownloadedFilesActionImpl : RefreshDownloadedAction {

    val episodesRepo by lazyInject { episodesRepo() }

    fun getAllEpisodes(): Observable<EpisodesModel> = episodesRepo.observeAll()
            .take(1)
            .flatMapIterable { it }

    fun checkEpisodeFile(episode: EpisodesModel, file: File): Completable {
        if (!file.exists())
            return Completable.complete()

        return episodesRepo
                .save(episode.id, episode.copy(file = file.absolutePath))
                .toCompletable()
    }

    override fun invoke(context: Context, params: Unit): Completable {
        return getAllEpisodes()
                .flatMapCompletable {
                    val file = File(context.filesDir, getFileName(it.audioUrl))
                    return@flatMapCompletable checkEpisodeFile(it, file)
                }
    }
}

interface DownloadEpisodeAction : IntentAction<DownloadEpisodeActionParams>
class DownloadEpisodeActionParams(val episodeId: Long)

class DownloadEpisodeActionImpl(
        val intentMaker: IntentMaker
) : DownloadEpisodeAction {

    override fun invoke(context: Context, params: DownloadEpisodeActionParams): Completable =
            EpisodeLoaderService.intent(params.episodeId, intentMaker, context).startService(context)
}

interface CancelDownloadEpisodeAction : IntentAction<Unit>

class CancelDownloadEpisodeActionImpl : CancelDownloadEpisodeAction, IntentMaker by Injector().intentMaker() {

    override fun invoke(context: Context, params: Unit): Completable = startBroadcast(context, CANCEL_DOWNLOAD_ACTION)

}