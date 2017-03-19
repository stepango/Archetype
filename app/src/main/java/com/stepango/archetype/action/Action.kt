package com.stepango.archetype.action

import android.content.Context
import io.reactivex.Completable

interface ActionHandler {
    fun handleAction(context: Context, actionId: Number, map: Args = argsOf())
    fun createAction(context: Context, actionId: Number, map: Args): Completable
    fun stopActions(): Completable = Completable.complete()
}

interface BaseActionHandler {
    fun handleAction(actionId: Number, map: Args = argsOf())
}