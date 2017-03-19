package com.stepango.archetype.rx

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

interface CompositeDisposableComponent {
    var composite: CompositeDisposable

    fun Disposable.bind(): Unit

    fun resetCompositeDisposable() {
        synchronized(this) {
            composite.clear()
            composite = CompositeDisposable()
        }
    }
}

class CompositeDisposableComponentImpl : CompositeDisposableComponent {
    override var composite = CompositeDisposable()

    override fun Disposable.bind() {
        composite.add(this)
    }
}