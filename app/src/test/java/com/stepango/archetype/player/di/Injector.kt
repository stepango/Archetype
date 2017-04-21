package com.stepango.archetype.player.di

import com.nhaarman.mockito_kotlin.mock

val Any.injector: Injector by lazy { InjectorImpl(mock()) }
