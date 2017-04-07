package com.stepango.archetype

import android.app.Application

class App : Application() {

    companion object {
        lateinit var instance: com.stepango.archetype.App
    }

    init {
        instance = this
    }
}