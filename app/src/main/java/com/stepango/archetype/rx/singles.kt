package com.stepango.archetype.rx

import io.reactivex.Single
import io.reactivex.functions.BiFunction

inline fun <T1, T2, R> zip(source: Single<T1>, target: Single<T2>, crossinline block: (T1, T2) -> R): Single<R>
        = Single.zip<T1, T2, R>(source, target, BiFunction { t1, t2 -> block(t1, t2) })
