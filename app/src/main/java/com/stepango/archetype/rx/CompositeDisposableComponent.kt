package com.stepango.archetype.rx

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


interface CompositeDisposableHolder {
    var composite: CompositeDisposable

    fun Disposable.bind(): Unit

    fun bindDisposable(disposable: Disposable): Unit

    fun resetCompositeDisposable() {
        synchronized(this) {
            composite.clear()
            composite = CompositeDisposable()
        }
    }
}

class CompositeDisposableHolderImpl : CompositeDisposableHolder {
    override var composite = CompositeDisposable()

    override fun bindDisposable(disposable: Disposable) {
        composite += disposable
    }

    override fun Disposable.bind() {
        bindDisposable(this)
    }
}

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    add(disposable)
}