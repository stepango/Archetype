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
    /** Log a verbose message with optional format args. */
    fun v(message: String, vararg args: Any)

    /** Log a verbose exception and a message with optional format args.  */
    fun v(t: Throwable?, message: String, vararg args: Any)

    fun d(message: String)
    /** Log a debug message with optional format args.  */
    fun d(message: String, vararg args: Any)

    /** Log a debug exception and a message with optional format args.  */
    fun d(t: Throwable?, message: String, vararg args: Any)

    fun i(message: String)
    /** Log an info message with optional format args.  */
    fun i(message: String, vararg args: Any)

    /** Log an info exception and a message with optional format args.  */
    fun i(t: Throwable?, message: String, vararg args: Any)

    fun w(message: String)
    /** Log a warning message with optional format args.  */
    fun w(message: String, vararg args: Any)

    /** Log a warning exception and a message with optional format args.  */
    fun w(t: Throwable?, message: String, vararg args: Any)

    fun e(message: String)
    /** Log an error message with optional format args.  */
    fun e(message: String, vararg args: Any)

    /** Log an error exception and a message with optional format args.  */
    fun e(t: Throwable?, message: String, vararg args: Any)
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
inline fun Logger.e(throwable: Throwable, msg: () -> Any?)
        = if (isErrorEnabled) e(throwable, msg().toString()) else Unit

/**
 * Lazy add a log message if isErrorEnabled is true
 */
inline fun Logger.e(msg: () -> Any?)
        = if (isErrorEnabled) e(msg().toString()) else Unit
