package com.stepango.archetype.action

import com.stepango.archetype.logger.d
import com.stepango.archetype.logger.logger
import com.stepango.archetype.player.network.Api
import com.stepango.archetype.rx.CompositeDisposableComponent
import com.stepango.archetype.rx.CompositeDisposableComponentImpl
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ApiActionHandler(
        val actionProducer: ActionProducer<ApiAction>
) :
        ActionHandler<Api>,
        CompositeDisposableComponent by CompositeDisposableComponentImpl(),
        ActionProducer<ApiAction> by actionProducer {

    override fun handleAction(context: Api, actionId: Int, args: Args) {
        val action = createAction(actionId)
        val observer = observer(action::class.java.simpleName)
        action.invoke(context, args)
                .subscribeOn(Schedulers.io())
                .subscribeWith(observer)
        if (action.isDisposable()) observer.disposable?.bind()
    }

    override fun stopActions(): Completable = Completable.fromAction {
        resetCompositeDisposable()
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