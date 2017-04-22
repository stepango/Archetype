package com.stepango.archetype

import android.app.Application

open class App : Application() {

    companion object {
        lateinit var instance: App
    }

    init {
        instance = this
    }
}