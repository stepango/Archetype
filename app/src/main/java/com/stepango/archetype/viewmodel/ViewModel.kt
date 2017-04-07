package com.stepango.archetype.viewmodel

import android.databinding.ObservableBoolean
import android.os.Parcelable
import com.stepango.archetype.action.Args
import com.stepango.archetype.action.argsOf
import com.stepango.archetype.bundle.putState
import com.stepango.archetype.logger.logger
import com.stepango.archetype.player.data.wrappers.ArgsHolder
import com.stepango.archetype.player.di.Injector
import com.stepango.archetype.player.di.lazyInject
import com.stepango.archetype.rx.CompositeDisposableComponent
import com.stepango.archetype.rx.CompositeDisposableComponentImpl
import com.stepango.archetype.ui.Toaster
import com.trello.navi2.Event
import com.trello.navi2.NaviComponent
import com.trello.navi2.rx.RxNavi
import io.mironov.smuggler.AutoParcelable
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers.io

private val onNextStub: (Any) -> Unit = {}
private val onErrorStub: (Throwable) -> Unit = { Injector().logger.e(it, "On error not implemented") }
private val onCompleteStub: () -> Unit = {}

interface ViewModel : NaviComponent, ArgsHolder, CompositeDisposableComponent, LoaderHolder {
    val toaster: Toaster

    override fun args(): Args

    fun <T : Any> Observable<T>.bindSubscribe(
            scheduler: Scheduler = io(),
            onNext: (T) -> Unit = onNextStub,
            onError: (Throwable) -> Unit = onErrorStub,
            onComplete: () -> Unit = onCompleteStub
    ) = subscribeOn(scheduler).subscribe(onNext, onError, onComplete).bind()

    fun <T : Any> Flowable<T>.bindSubscribe(
            scheduler: Scheduler = io(),
            onNext: (T) -> Unit = onNextStub,
            onError: (Throwable) -> Unit = onErrorStub,
            onComplete: () -> Unit = onCompleteStub
    ) = subscribeOn(scheduler).subscribe(onNext, onError, onComplete).bind()

    fun <T : Any> Single<T>.bindSubscribe(
            scheduler: Scheduler = io(),
            onSuccess: (T) -> Unit = onNextStub,
            onError: (Throwable) -> Unit = onErrorStub
    ) = subscribeOn(scheduler).subscribe(onSuccess, onError).bind()

    fun <T : Any> Maybe<T>.bindSubscribe(
            scheduler: Scheduler = io(),
            onSuccess: (T) -> Unit = onNextStub,
            onError: (Throwable) -> Unit = onErrorStub,
            onComplete: () -> Unit = onCompleteStub
    ) = subscribeOn(scheduler).subscribe(onSuccess, onError, onComplete).bind()

    fun Completable.bindSubscribe(
            scheduler: Scheduler = io(),
            onComplete: () -> Unit = onCompleteStub,
            onError: (Throwable) -> Unit = onErrorStub
    ) = subscribeOn(scheduler).subscribe(onComplete, onError).bind()

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
        inline val state: Parcelable = object : AutoParcelable {}
) :
        ViewModel,
        NaviComponent by naviComponent,
        CompositeDisposableComponent by CompositeDisposableComponentImpl(),
        LoaderHolder by LoaderHolderImpl() {

    override val toaster by lazyInject { toaster() }
    override fun args(): Args = args

    init {
        observe(event).bindSubscribe(onNext = { resetCompositeDisposable() })
        observe(Event.SAVE_INSTANCE_STATE).bindSubscribe(onNext = { it.putState(state) })
    }

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
