package com.stepango.archetype.viewmodel

import android.os.Parcelable
import com.stepango.archetype.action.ActionData
import com.stepango.archetype.action.Args
import com.stepango.archetype.action.ContextActionHandler
import com.stepango.archetype.action.argsOf
import com.stepango.archetype.bundle.ViewModelStateStub
import com.stepango.archetype.bundle.putState
import com.stepango.archetype.ui.Toaster
import com.trello.navi2.Event

interface ViewModel : LoaderHolder, RxLifecycle, LoadingProgressHelper {

    val toaster: Toaster
    val actionHandler: ContextActionHandler

    fun args(): Args

    fun <P : Any> executeAction(producer: () -> ActionData<P>) =
            producer().let {
                actionHandler.handleAction(it.action, it.params)
            }
}

class ViewModelParamsHolder(
        val rxLifecycle: RxLifecycle,
        val loaderHolder: LoaderHolder,
        val loadingProgressHelper: LoadingProgressHelper,
        val args: Args,
        val toaster: Toaster,
        val actionHandler: ContextActionHandler
)

@Suppress("AddVarianceModifier")
class StatefulViewModel<T : Parcelable>(
        rxLifecycle: RxLifecycle,
        loaderHolder: LoaderHolder,
        loadingProgressHelper: LoadingProgressHelper,
        args: Args,
        val state: T,
        toaster: Toaster,
        actionHandler: ContextActionHandler
) : ViewModel by ViewModelImpl(
        args = args,
        state = state,
        toaster = toaster,
        actionHandler = actionHandler,
        rxLifecycle = rxLifecycle,
        loaderHolder = loaderHolder,
        loadingProgressHelper = loadingProgressHelper) {

    constructor(params: ViewModelParamsHolder, state: T) : this(
            rxLifecycle = params.rxLifecycle,
            loaderHolder = params.loaderHolder,
            loadingProgressHelper = params.loadingProgressHelper,
            args = params.args,
            state = state,
            toaster = params.toaster,
            actionHandler = params.actionHandler
    )
}

class ViewModelImpl constructor(
        rxLifecycle: RxLifecycle,
        loaderHolder: LoaderHolder,
        loadingProgressHelper: LoadingProgressHelper,
        inline val args: Args = argsOf(),
        inline val state: Parcelable = ViewModelStateStub.INSTANCE,
        override val toaster: Toaster,
        override val actionHandler: ContextActionHandler
) :
        ViewModel,
        RxLifecycle by rxLifecycle,
        LoaderHolder by loaderHolder,
        LoadingProgressHelper by loadingProgressHelper {

    init {
        observe(Event.SAVE_INSTANCE_STATE).bindSubscribeTillDetach(onNext = { it.putState(state) })
        observe(Event.DESTROY).bindSubscribeTillDetach(onNext = {
            actionHandler.stopActions()
        })
    }

    override fun args(): Args = args

}