package com.stepango.archetype.action

import io.reactivex.Completable

interface ActionProducer<out T : Action<*>> {
    fun createAction(actionId: Number): T
}

interface ActionHandler<in T> {
    fun handleAction(context: T, actionId: Number, args: Args = argsOf())
    fun stopActions(): Completable
}

interface BaseActionHandler {
    fun handleAction(actionId: Number, args: Args = argsOf()): Unit
    operator fun invoke(actionId: Number, args: Args = argsOf()) = handleAction(actionId, args)
}
