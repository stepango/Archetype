package com.stepango.archetype.logger

interface Logger {

    val isDebugEnabled: Boolean
        get() = false
    val isInfoEnabled: Boolean
        get() = false
    val isWarnEnabled: Boolean
        get() = false
    val isErrorEnabled: Boolean
        get() = false

    fun v(message: String)

    fun d(message: String)

    fun i(message: String)

    fun w(message: String)

    fun e(message: String)

    fun e(e: Throwable?, message: String)
}

/**
 * Lazy add a log message if isDebugEnabled is true
 */
inline fun Logger.d(msg: () -> Any?)
        = if (isDebugEnabled) d(msg().toString()) else Unit

/**
 * Lazy add a log message if isInfoEnabled is true
 */
inline fun Logger.i(msg: () -> Any?)
        = if (isInfoEnabled) i(msg().toString()) else Unit

/**
 * Lazy add a log message if isWarnEnabled is true
 */
inline fun Logger.w(msg: () -> Any?)
        = if (isWarnEnabled) w(msg().toString()) else Unit

/**
 * Lazy add a log message if isErrorEnabled is true
 */
inline fun Logger.e(msg: () -> Any?)
        = if (isErrorEnabled) e(msg().toString()) else Unit
