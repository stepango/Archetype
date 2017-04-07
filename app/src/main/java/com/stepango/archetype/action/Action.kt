package com.stepango.archetype.action

import android.content.Context
import com.stepango.archetype.R
import io.reactivex.Completable

interface Action<in T> {
    fun isDisposable() = true
    fun invoke(context: T, args: Args): Completable
}

interface ContextAction : Action<Context> {

    val id: Number

    fun keys(): Array<String> = arrayOf()

    /**
     * Action should perform with [Context]
     */
    override operator fun invoke(context: Context, args: Args): Completable

}

class IdleAction : ContextAction {

    override val id = R.id.action_idle

    override fun invoke(context: Context, args: Args): Completable = Completable.complete()

}
