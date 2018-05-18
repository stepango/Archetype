package com.stepango.archetype.action

import android.content.Context
import io.reactivex.Completable

interface ContextAction<P> {
    fun isDisposable() = true
    operator fun invoke(context: Context, params: P): Completable
}

interface IDAction : ContextAction<Long>

class IdleAction : ContextAction<Unit> {
    override fun invoke(context: Context, params: Unit): Completable = Completable.complete()
}

data class ActionData<P>(val action: ContextAction<P>, val params: P) {

    @Suppress("UNCHECKED_CAST")
    fun asHolder() = object : ActionDataHolder {
        override fun actionData(): ActionData<Any>? = this@ActionData as ActionData<Any>
    }

    companion object {
        val IDLE = ActionData(IdleAction(), Unit)
    }
}

data class NamedActionData<T>(val name: String, val action: ActionData<T>) {
    override fun toString() = name

    companion object {
        val IDLE = NamedActionData("", ActionData.IDLE)
    }
}

interface ActionDataHolder {
    fun actionData(): ActionData<Any>?
}

interface ActionHandlerHolder {
    val actionHandler: ContextActionHandler
}

fun <P : Any> ContextAction<P>.with(param: P): ActionData<P> = ActionData(this, param)
fun <P : Any> ActionData<P>.execute(ah: ContextActionHandler) = ah.handleAction(action, params)
fun ContextAction<Unit>.noParams(): ActionData<Unit> = ActionData(this, Unit)