package com.stepango.archetype.viewmodel

import android.app.Activity
import com.stepango.archetype.logger.d
import com.stepango.archetype.logger.logger
import com.stepango.archetype.rx.CompositeDisposableHolder
import com.stepango.archetype.rx.CompositeDisposableHolderImpl
import com.trello.navi2.Event
import com.trello.navi2.NaviComponent
import com.trello.navi2.model.ActivityResult
import com.trello.navi2.rx.RxNavi
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single


interface RxLifecycle : CompositeDisposableHolder, NaviComponent {

    val startObservable: Observable<out Any>
    val vmLifeLongSubscription: CompositeDisposableHolder

    fun <T : Any> Observable<T>.bindSubscribe(
            onNext: (T) -> Unit = onNextStub,
            onError: (Throwable) -> Unit = onErrorStub,
            onComplete: () -> Unit = onCompleteStub
    ) = startObservable.doOnNext {
        this.subscribe(onNext, onError, onComplete).bind()
    }.bindSubscribeTillDetach()

    fun <T : Any> Flowable<T>.bindSubscribe(
            onNext: (T) -> Unit = onNextStub,
            onError: (Throwable) -> Unit = onErrorStub,
            onComplete: () -> Unit = onCompleteStub
    ) = startObservable.doOnNext {
        this.subscribe(onNext, onError, onComplete).bind()
    }.bindSubscribeTillDetach()

    fun <T : Any> Single<T>.bindSubscribe(
            onSuccess: (T) -> Unit = onNextStub,
            onError: (Throwable) -> Unit = onErrorStub
    ) = startObservable.doOnNext {
        this.subscribe(onSuccess, onError).bind()
    }.bindSubscribeTillDetach()

    fun <T : Any> Maybe<T>.bindSubscribe(
            onSuccess: (T) -> Unit = onNextStub,
            onError: (Throwable) -> Unit = onErrorStub,
            onComplete: () -> Unit = onCompleteStub
    ) = startObservable.doOnNext {
        this.subscribe(onSuccess, onError, onComplete).bind()
    }.bindSubscribeTillDetach()

    fun Completable.bindSubscribe(
            onComplete: () -> Unit = onCompleteStub,
            onError: (Throwable) -> Unit = onErrorStub
    ) = startObservable.doOnNext {
        this.subscribe(onComplete, onError).bind()
    }.bindSubscribeTillDetach()

    fun <T : Any> Observable<T>.bindSubscribeTillDetach(
            onNext: (T) -> Unit = onNextStub,
            onError: (Throwable) -> Unit = onErrorStub,
            onComplete: () -> Unit = onCompleteStub
    ) = subscribe(onNext, onError, onComplete).let(vmLifeLongSubscription::bindDisposable)

    fun <T : Any> Flowable<T>.bindSubscribeTillDetach(
            onNext: (T) -> Unit = onNextStub,
            onError: (Throwable) -> Unit = onErrorStub,
            onComplete: () -> Unit = onCompleteStub
    ) = subscribe(onNext, onError, onComplete).let(vmLifeLongSubscription::bindDisposable)

    fun <T : Any> Single<T>.bindSubscribeTillDetach(
            onSuccess: (T) -> Unit = onNextStub,
            onError: (Throwable) -> Unit = onErrorStub
    ) = subscribe(onSuccess, onError).let(vmLifeLongSubscription::bindDisposable)

    fun <T : Any> Maybe<T>.bindSubscribeTillDetach(
            onSuccess: (T) -> Unit = onNextStub,
            onError: (Throwable) -> Unit = onErrorStub,
            onComplete: () -> Unit = onCompleteStub
    ) = subscribe(onSuccess, onError, onComplete).let(vmLifeLongSubscription::bindDisposable)

    fun Completable.bindSubscribeTillDetach(
            onComplete: () -> Unit = onCompleteStub,
            onError: (Throwable) -> Unit = onErrorStub
    ) = subscribe(onComplete, onError).let(vmLifeLongSubscription::bindDisposable)

    fun ViewModel.observeActivityResult(requestCode: Int, resultCode: Int = Activity.RESULT_OK, onResult: (ActivityResult) -> Unit)
    fun ViewModel.activityResultObservable(requestCode: Int, resultCode: Int = Activity.RESULT_OK): Observable<ActivityResult>
}

fun <T : Any> NaviComponent.observe(event: Event<T>): Observable<T> = RxNavi.observe(this, event)

class RxLifecycleImpl(
        naviComponent: NaviComponent,
        compositeDisposableHolder: CompositeDisposableHolder,
        startEvent: Event<*>,
        stopEvent: Event<*>,
        detachEvent: Event<*>
) : RxLifecycle,
        NaviComponent by naviComponent,
        CompositeDisposableHolder by compositeDisposableHolder {

    override val startObservable: Observable<out Any> = observe(startEvent).cacheWithInitialCapacity(1)
    override val vmLifeLongSubscription: CompositeDisposableHolder = CompositeDisposableHolderImpl()

    init {
        observe(detachEvent).bindSubscribeTillDetach(onNext = { vmLifeLongSubscription.resetCompositeDisposable() })
        observe(stopEvent).bindSubscribeTillDetach(onNext = { resetCompositeDisposable() })
        startObservable
        startObservable.bindSubscribeTillDetach()
    }

    override fun ViewModel.observeActivityResult(requestCode: Int, resultCode: Int, onResult: (ActivityResult) -> Unit) = activityResultObservable(requestCode, resultCode)
            .doOnNext { showLoader() }
            .doOnComplete(this::hideLoader)
            .bindSubscribeTillDetach(onNext = onResult)

    override fun ViewModel.activityResultObservable(requestCode: Int, resultCode: Int): Observable<ActivityResult> = this.observe(Event.ACTIVITY_RESULT)
            .doOnNext { logger.d { "LIFECYCLE:: ACTIVITY_RESULT $it" } }
            .filter { it.resultCode() == resultCode }
            .filter { it.requestCode() == requestCode }
}