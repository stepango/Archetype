package com.stepango.archetype.logger

import android.util.Log
import com.stepango.archetype.BuildConfig
import com.stepango.archetype.logger.Logger

class SimpleLogger : Logger {

    override val isDebugEnabled: Boolean = BuildConfig.DEBUG
    override val isInfoEnabled: Boolean = BuildConfig.DEBUG
    override val isWarnEnabled: Boolean = BuildConfig.DEBUG
    override val isErrorEnabled: Boolean = BuildConfig.DEBUG

    override fun v(message: String) {
        Log.v("Logger", message)
    }

    override fun d(message: String) {
        Log.d("Logger", message)
    }

    override fun i(message: String) {
        Log.i("Logger", message)
    }

    override fun w(message: String) {
        Log.w("Logger", message)
    }

    override fun e(message: String) {
        Log.e("Logger", message)
    }

    override fun e(e: Throwable?, message: String) {
        Log.e("Logger", "$message. Message: ${e?.message}")
    }

}