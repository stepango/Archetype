package com.stepango.archetype.action

import android.content.Context
import io.reactivex.Completable

interface ActionHandler {
    fun handleAction(context: Context, actionId: Int, map: Args = argsOf())
    fun createAction(context: Context, actionId: Int, map: Args): Completable
    fun stopActions(): Completable = Completable.complete()
}

interface BaseActionHandler {
    fun handleAction(actionId: Int, map: Args = argsOf())
}

interface ContextAction {

    fun isDisposable() = true

    /**
     * Action should perform with [Context]
     */
    operator fun invoke(context: Context, args: Args = argsOf()): Completable

}
