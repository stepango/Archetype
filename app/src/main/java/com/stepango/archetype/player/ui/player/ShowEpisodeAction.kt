package com.stepango.archetype.player.ui.player

import android.content.Context
import com.stepango.archetype.action.IntentAction
import com.stepango.archetype.action.IntentMaker
import com.stepango.archetype.action.start
import com.stepango.archetype.logger.d
import com.stepango.archetype.logger.logger
import com.stepango.archetype.player.di.Injector
import io.reactivex.Completable

interface ShowEpisodeAction : IntentAction<ShowEpisodeActionParams>
class ShowEpisodeActionParams(val episodeId: Long)

class ShowEpisodeActionImpl : ShowEpisodeAction, IntentMaker by Injector().intentMaker() {

    override fun invoke(context: Context, params: ShowEpisodeActionParams): Completable =
            PlayerActivity.intent(params.episodeId, this, context).start(context)
                    .doOnComplete { logger.d { "aaa" } }

}