package com.stepango.archetype.action

import android.content.Context
import android.util.SparseArray
import com.stepango.archetype.logger.d
import com.stepango.archetype.logger.logger
import com.stepango.archetype.rx.CompositeDisposableComponent
import com.stepango.archetype.rx.CompositeDisposableComponentImpl
import com.stepango.archetype.util.name
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers.io

class ContextActionHandler(
        actionsProducer: ActionProducer<ContextAction>
) :
        ActionHandler<Context>,
        ActionProducer<ContextAction> by actionsProducer,
        CompositeDisposableComponent by CompositeDisposableComponentImpl() {

    override fun stopActions(): Completable = Completable.fromAction {
        resetCompositeDisposable()
    }

    override fun handleAction(context: Context, actionId: Number, args: Args) {
        val actionName = actionId.name(context)
        logger.d { "Action:: $actionName" }
        val observer = observer(actionName)
        val action = createAction(actionId)
        //        if (action.keys().isNotEmpty()) args.hasKeysGuarg(*action.keys())
        action.invoke(context, args)
                .subscribeOn(io())
                .subscribeWith(observer)
        if (action.isDisposable()) observer.disposable?.bind()
    }

    private fun observer(actionName: String) = object : CompletableObserver {
        var disposable: Disposable? = null

        override fun onComplete() {
            logger.d { "$actionName - completed successfully" }
            composite.remove(disposable)
        }

        override fun onSubscribe(d: Disposable) {
            disposable = d
        }

        override fun onError(e: Throwable?) {
            logger.e(e, "$actionName - completed with error")
        }
    }
}

class ActionProducerImpl(val context: Context, val actions: SparseArray<ContextAction>) : ActionProducer<ContextAction> {
    override fun createAction(actionId: Number)
            = actions[actionId.toInt()] ?: throw IllegalArgumentException("${actionId.name(context)} not found")
}