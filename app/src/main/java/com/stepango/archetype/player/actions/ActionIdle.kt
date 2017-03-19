package com.stepango.archetype.player.actions

import android.content.Context
import com.stepango.archetype.action.Args
import com.stepango.archetype.action.ContextAction
import io.reactivex.Completable

class IdleAction : ContextAction {
    override fun invoke(context: Context, args: Args): Completable = Completable.complete()
}