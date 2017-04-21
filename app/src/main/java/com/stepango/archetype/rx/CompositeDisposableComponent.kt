package com.stepango.archetype.rx

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

interface CompositeDisposableComponent {
    val composite: CompositeDisposable

    fun Disposable.bind() = composite.add(this)

    fun resetCompositeDisposable() {
        composite.clear()
    }
}

class CompositeDisposableComponentImpl : CompositeDisposableComponent {
    override var composite = CompositeDisposable()
}