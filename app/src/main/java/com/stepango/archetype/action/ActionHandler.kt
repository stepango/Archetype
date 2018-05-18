package com.stepango.archetype.action

import android.content.Context
import com.stepango.archetype.rx.CompositeDisposableHolder
import io.reactivex.Completable

interface ContextActionHandlerFactory {
    fun createActionHandler(context: Context, compositeDisposableHolder: CompositeDisposableHolder): ContextActionHandler
}

interface ContextActionHandler {
    fun stopActions(): Completable
    fun <P : Any> handleAction(contextAction: ContextAction<P>, params: P)
    fun <P : Any> createAction(contextAction: ContextAction<P>, params: P): Completable
}