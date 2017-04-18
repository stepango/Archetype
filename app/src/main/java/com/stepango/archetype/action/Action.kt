package com.stepango.archetype.action

import android.content.Context
import io.reactivex.Completable

interface Action<in T> {
    fun isDisposable() = true
    fun invoke(context: T, args: Args): Completable
}

interface ContextAction : Action<Context> {

    /**
     * Action should perform with [Context]
     */
    override operator fun invoke(context: Context, args: Args): Completable

}

class IdleAction : ContextAction {
    override fun invoke(context: Context, args: Args): Completable = Completable.complete()
}
