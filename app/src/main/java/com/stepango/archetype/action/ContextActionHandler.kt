package com.stepango.archetype.action

import android.content.Context
import com.stepango.archetype.logger.d
import com.stepango.archetype.logger.logger
import com.stepango.archetype.rx.CompositeDisposableHolder
import com.stepango.archetype.rx.actionScheduler
import com.stepango.archetype.rx.nonDisposableActionScheduler
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable

class ContextActionHandlerRealFactory : ContextActionHandlerFactory {
    override fun createActionHandler(context: Context, compositeDisposableHolder: CompositeDisposableHolder): ContextActionHandler = ContextActionHandlerImpl(context, compositeDisposableHolder)
}

class ContextActionHandlerImpl(
        val context: Context,
        val compositeDisposableHolder: CompositeDisposableHolder
) :
        CompositeDisposableHolder by compositeDisposableHolder, ContextActionHandler {

    override fun stopActions(): Completable = Completable.fromAction {
        resetCompositeDisposable()
    }

    inline fun <reified T : Any> ContextActionHandler.execute(data: ActionData<T>) =
            handleAction(data.action, data.params)

    override fun <P : Any> createAction(contextAction: ContextAction<P>, params: P): Completable =
            contextAction.invoke(context, params).doOnSubscribe { logger.d { "Action:: createAction ${contextAction::class.java.name}" } }

    override fun <P : Any> handleAction(contextAction: ContextAction<P>, params: P) {
        val actionName = contextAction::class.java.name
        logger.d { "Action:: handle $actionName" }
        val observer = observer(actionName)
        val isDisposable = contextAction.isDisposable()
        contextAction.invoke(context, params)
                .subscribeOn(if (isDisposable) actionScheduler else nonDisposableActionScheduler)
                .subscribeWith(observer)
        if (isDisposable) observer.disposable?.bind()
    }

    private fun observer(actionName: String) = object : CompletableObserver {
        var disposable: Disposable? = null

        override fun onComplete() {
            logger.d { "$actionName - completed successfully" }
            disposable?.let(composite::remove)
        }

        override fun onSubscribe(d: Disposable) {
            disposable = d
        }

        override fun onError(e: Throwable) {
            logger.e(e, "$actionName - completed with error")
        }
    }
}