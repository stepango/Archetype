package com.stepango.archetype.action

import io.reactivex.Completable

interface ActionProducer<out T : Action<*>> {
    fun createAction(actionId: Int): T
}

interface ActionHandler<in T> {
    fun handleAction(context: T, actionId: Int, args: Args = argsOf())
    fun stopActions(): Completable
}

interface BaseActionHandler {
    fun handleAction(actionId: Int, args: Args = argsOf()): Unit
    operator fun invoke(actionId: Int, args: Args = argsOf()) = handleAction(actionId, args)
}
