package com.stepango.archetype.player.loader

import android.content.Context
import com.stepango.archetype.action.*
import com.stepango.archetype.logger.logger
import com.stepango.archetype.player.data.db.EpisodesModelRepo
import com.stepango.archetype.player.data.db.model.EpisodesModel
import com.stepango.archetype.player.di.Injector
import com.stepango.archetype.player.di.lazyInject
import com.stepango.archetype.util.getFileName
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers.io
import java.io.File

/**
 * Wild, 02.07.2017.
 */

class RefreshDownloadedFilesAction : ContextAction {

    val episodesRepo by lazyInject { episodesRepo() }

    override fun invoke(context: Context, args: Args): Completable
            = episodesRepo.getAll()
            .subscribeOn(io())
            .flatMapCompletable {
                val file = File(context.filesDir, getFileName(it.audioUrl))
                if (file.exists()) {
                    logger.d("file exists for ${it.name}")
                    it.file = file.absolutePath
                    return@flatMapCompletable episodesRepo.save(it.id, it).toCompletable()
                } else {
                    logger.d("file not found for ${it.name}")
                    return@flatMapCompletable Completable.complete()
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