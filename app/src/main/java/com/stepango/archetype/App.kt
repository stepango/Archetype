package com.stepango.archetype

import android.app.Application
import com.stepango.archetype.rx.actionScheduler
import com.stepango.archetype.rx.networkScheduler
import com.stepango.archetype.rx.nonDisposableActionScheduler
import com.stepango.archetype.rx.uiScheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

open class App : Application() {

    companion object {
        lateinit var instance: App
    }

    init {
        instance = this
        initSchedulers()
    }

    private fun initSchedulers() {
        networkScheduler = Schedulers.io()
        actionScheduler = Schedulers.io()
        nonDisposableActionScheduler = Schedulers.computation()
        uiScheduler = AndroidSchedulers.mainThread()
    }
}