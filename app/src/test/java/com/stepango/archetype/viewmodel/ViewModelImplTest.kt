package com.stepango.archetype.viewmodel

import com.nhaarman.mockito_kotlin.mock
import com.stepango.archetype.player.di.injector
import com.trello.navi2.internal.NaviEmitter
import org.junit.Before
import org.junit.Test

class ViewModelImplTest {

    @Before fun init(){

    }

    @Test fun onDetach() {
        val naviComponent = NaviEmitter.createFragmentEmitter()
        val viewModel = ViewModelImpl(naviComponent)
        val test = viewModel.onTerminalEventObserver()
                .test()

        naviComponent.onDetach()

        test.assertValueCount(1)
    }

    @Test fun onSaveInstantState() {
        val naviComponent = NaviEmitter.createFragmentEmitter()
        val viewModel = ViewModelImpl(naviComponent)
        val test = viewModel.saveInstantStateObserver()
                .test()

        naviComponent.onSaveInstanceState(mock())

        test.assertValueCount(1)
    }

}