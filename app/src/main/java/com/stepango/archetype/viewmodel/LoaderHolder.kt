package com.stepango.archetype.viewmodel

import android.databinding.ObservableBoolean

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