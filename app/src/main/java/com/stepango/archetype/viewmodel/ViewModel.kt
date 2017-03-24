package com.ninetyseconds.auckland.core.viewmodel

import android.databinding.ObservableBoolean
import android.os.Parcelable
import com.ninetyseconds.auckland.aa.ArgsHolder
import com.ninetyseconds.auckland.core.bundle.ViewModelStateStub
import com.ninetyseconds.auckland.core.bundle.putState
import com.ninetyseconds.auckland.core.rx.CompositeDisposableHolder
import com.ninetyseconds.auckland.core.rx.CompositeDisposableHolderImpl
import com.ninetyseconds.auckland.core.toast.Toaster
import com.ninetyseconds.auckland.di.Injector
import com.ninetyseconds.auckland.di.lazyInject
import com.ninetyseconds.auckland.marketplace.Args
import com.ninetyseconds.auckland.marketplace.argsOf
import com.trello.navi2.Event
import com.trello.navi2.NaviComponent
import com.trello.navi2.rx.RxNavi
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

private val onNextStub: (Any) -> Unit = {}
private val onErrorStub: (Throwable) -> Unit = { Injector().logger.e(it, "On error not implemented") }
private val onCompleteStub: () -> Unit = {}

interface ViewModel : NaviComponent, ArgsHolder, CompositeDisposableHolder, LoaderHolder {

    val toaster: Toaster

    override fun args(): Args

    fun <T : Any> Observable<T>.bindSubscribe(
            onNext: (T) -> Unit = onNextStub,
            onError: (Throwable) -> Unit = onErrorStub,
            onComplete: () -> Unit = onCompleteStub
    ) = subscribe(onNext, onError, onComplete).bind()

    fun <T : Any> Flowable<T>.bindSubscribe(
            onNext: (T) -> Unit = onNextStub,
            onError: (Throwable) -> Unit = onErrorStub,
            onComplete: () -> Unit = onCompleteStub
    ) = subscribe(onNext, onError, onComplete).bind()

    fun <T : Any> Single<T>.bindSubscribe(
            onSuccess: (T) -> Unit = onNextStub,
            onError: (Throwable) -> Unit = onErrorStub
    ) = subscribe(onSuccess, onError).bind()

    fun <T : Any> Maybe<T>.bindSubscribe(
            onSuccess: (T) -> Unit = onNextStub,
            onError: (Throwable) -> Unit = onErrorStub,
            onComplete: () -> Unit = onCompleteStub
    ) = subscribe(onSuccess, onError, onComplete).bind()

    fun Completable.bindSubscribe(
            onComplete: () -> Unit = onCompleteStub,
            onError: (Throwable) -> Unit = onErrorStub
    ) = subscribe(onComplete, onError).bind()

}

interface LoaderHolder {
    val showLoader: ObservableBoolean
    val isDataLoaded: ObservableBoolean

    fun showLoader() {
        showLoader.set(true)
    }

    fun hideLoader() {
        showLoader.set(false)
        showLoader.notifyChange()
    }
}

class LoaderHolderImpl(
        override val showLoader: ObservableBoolean = ObservableBoolean(true),
        override val isDataLoaded: ObservableBoolean = ObservableBoolean(false)
) : LoaderHolder

class ViewModelImpl(
        naviComponent: NaviComponent,
        event: Event<*> = Event.DETACH,
        inline val args: Args = argsOf(),
        inline val state: Parcelable = ViewModelStateStub.INSTANCE
) :
        ViewModel,
        NaviComponent by naviComponent,
        CompositeDisposableHolder by CompositeDisposableHolderImpl(),
        LoaderHolder by LoaderHolderImpl() {

    override val toaster by lazyInject { toaster }

    init {
        observe(event).bindSubscribe(onNext = { resetCompositeDisposable() })
        observe(Event.SAVE_INSTANCE_STATE).bindSubscribe(onNext = { it.putState(state) })
    }

    override fun args(): Args = args

    companion object {
        operator fun invoke(naviComponent: NaviComponent)
                = ViewModelImpl(naviComponent = naviComponent)

        operator fun invoke(naviComponent: NaviComponent, args: Args)
                = ViewModelImpl(naviComponent = naviComponent, args = args)

        operator fun invoke(naviComponent: NaviComponent, event: Event<*>)
                = ViewModelImpl(naviComponent = naviComponent, event = event)

        operator fun invoke(naviComponent: NaviComponent, event: Event<*>, state: Parcelable)
                = ViewModelImpl(naviComponent = naviComponent, event = event, state = state)
    }

}

fun <T : Any> NaviComponent.observe(event: Event<T>) = RxNavi.observe(this, event)
