package com.stepango.archetype.logger

import com.stepango.archetype.player.di.lazyInject

val loggerImpl by lazyInject { logger() }

@Suppress("unused") val Any.logger: Logger
    get() = loggerImpl

inline fun Throwable?.logError(block: () -> Any?) = loggerImpl.e(this, block().toString())
fun String?.logDebug(tag: String) = loggerImpl.d("$tag:: $this")
