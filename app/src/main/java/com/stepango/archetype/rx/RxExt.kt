package com.stepango.archetype.rx

import com.stepango.koptional.Optional
import io.reactivex.Maybe
import io.reactivex.MaybeSource
import io.reactivex.Observable
import io.reactivex.Observable.fromIterable
import io.reactivex.functions.BiFunction

fun <T : Any> Observable<List<T>>.flatten(): Observable<T> = flatMap { fromIterable(it) }

inline fun <T, U, R> Maybe<T>.zipWith(other: MaybeSource<out U>, crossinline zipper: (T, U) -> R): Maybe<R>
        = zipWith(other, BiFunction { t1, t2 -> zipper(t1, t2) })

fun <T : Collection<Any>> Observable<T>.filterNotEmpty(): Observable<T> = filter { it?.isNotEmpty() ?: false }
fun <T : Any> Observable<out Optional<out T>>.filterNonEmpty(): Observable<T> = filter { it.isPresent }.map { it.get() }

