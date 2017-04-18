package com.stepango.archetype.player.ui.player

import android.content.Context
import com.stepango.archetype.R
import com.stepango.archetype.action.Args
import com.stepango.archetype.action.IntentAction
import com.stepango.archetype.action.IntentMaker
import com.stepango.archetype.action.startIntent
import com.stepango.archetype.player.di.Injector
import com.stepango.archetype.player.ui.episodes.PlayerActivity
import io.reactivex.Completable


class ShowEpisodeAction : IntentAction, IntentMaker by Injector().intentMaker() {

    override fun invoke(context: Context, args: Args): Completable
            = startIntent<PlayerActivity>(context, args)

}