package com.stepango.archetype.player.loader

import android.content.Context
import com.stepango.archetype.action.IntentAction
import com.stepango.archetype.action.ContextAction
import com.stepango.archetype.action.Args
import com.stepango.archetype.action.IntentMaker
import com.stepango.archetype.action.startService
import com.stepango.archetype.action.startBroadcast
import com.stepango.archetype.logger.logger
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
            = episodesRepo.observeAll()
            .take(1)
            .flatMapIterable { it }
            .subscribeOn(io())
            .flatMapCompletable {
                val file = File(context.filesDir, getFileName(it.audioUrl))
                if (file.exists()) {
                    logger.d("file exists for ${it.name}")
                    return@flatMapCompletable episodesRepo.save(it.id, it.copy(file = file.absolutePath)).toCompletable()
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