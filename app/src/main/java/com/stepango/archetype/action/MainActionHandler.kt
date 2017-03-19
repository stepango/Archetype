package com.stepango.archetype.action

import android.content.Context
import android.util.SparseArray
import com.stepango.archetype.logger.d
import com.stepango.archetype.logger.logger
import com.stepango.archetype.resources.name
import com.stepango.archetype.rx.CompositeDisposableComponent
import com.stepango.archetype.rx.CompositeDisposableComponentImpl
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable

class MainActionHandler(
        val actions: SparseArray<ContextAction>
) :
        ActionHandler,
        CompositeDisposableComponent by CompositeDisposableComponentImpl() {

    override fun stopActions(): Completable = Completable.fromAction {
        resetCompositeDisposable()
    }

    override fun handleAction(context: Context, actionId: Int, map: Args) {
        val actionName = actionId.name(context)
        val observer = observer(actionName)
        createAction(context, actionId, map).subscribeWith(observer)
        if (contextAction(actionId, actionName).isDisposable()) observer.disposable?.bind()
    }

    override fun createAction(context: Context, actionId: Int, map: Args): Completable {
        val actionName = actionId.name(context)
        logger.d { "Action:: $actionName" }
        val contextAction = contextAction(actionId, actionName)
        return contextAction.invoke(context, map)
    }

    private fun contextAction(actionId: Int, actionName: String): ContextAction
            = actions[actionId] ?: throw IllegalArgumentException("$actionName not found")

    private fun observer(actionName: String) = object : CompletableObserver {
        var disposable: Disposable? = null

        override fun onComplete() {
            logger.d { "$actionName - completed successfully" }
            composite.remove(disposable)
        }

        override fun onSubscribe(d: Disposable) {
            disposable = d
        }

        override fun onError(e: Throwable?) = logger.e(e, "$actionName - completed with error")
    }
}