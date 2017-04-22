package com.stepango.archetype.viewmodel

import com.stepango.archetype.logger.logger
import com.stepango.archetype.player.di.Injector

val onNextStub: (Any) -> Unit = {}
val onErrorStub: (Throwable) -> Unit = { Injector().logger.e(it, "On error not implemented") }
val onCompleteStub: () -> Unit = {}