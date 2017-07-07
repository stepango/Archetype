package com.stepango.archetype.rx

import com.stepango.archetype.viewmodel.onCompleteStub
import com.stepango.archetype.viewmodel.onErrorStub
import com.stepango.archetype.viewmodel.onNextStub
import com.stepango.koptional.Optional
import io.reactivex.*
import io.reactivex.Observable.fromIterable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers.io

fun <T : Any> Observable<List<T>>.flatten(): Observable<T> = flatMap { fromIterable(it) }

inline fun <T, U, R> Maybe<T>.zipWith(other: MaybeSource<out U>, crossinline zipper: (T, U) -> R): Maybe<R>
        = zipWith(other, BiFunction { t1, t2 -> zipper(t1, t2) })

fun <T : Collection<Any>> Observable<T>.filterNotEmpty(): Observable<T> = filter { it?.isNotEmpty() ?: false }
fun <T : Any> Observable<out Optional<out T>>.filterNonEmpty(): Observable<T> = filter { it.isPresent }.map { it.get() }

fun <T : Any> Observable<T>.subscribeBy(
        scheduler: Scheduler = io(),
        onNext: (T) -> Unit = onNextStub,
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub
): Disposable = subscribeOn(scheduler).subscribe(onNext, onError, onComplete)

fun <T : Any> Flowable<T>.subscribeBy(
        scheduler: Scheduler = io(),
        onNext: (T) -> Unit = onNextStub,
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub
): Disposable = subscribeOn(scheduler).subscribe(onNext, onError, onComplete)

fun <T : Any> Single<T>.subscribeBy(
        scheduler: Scheduler = io(),
        onSuccess: (T) -> Unit = onNextStub,
        onError: (Throwable) -> Unit = onErrorStub
): Disposable = subscribeOn(scheduler).subscribe(onSuccess, onError)

fun <T : Any> Maybe<T>.subscribeBy(
        scheduler: Scheduler = io(),
        onSuccess: (T) -> Unit = onNextStub,
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub
): Disposable = subscribeOn(scheduler).subscribe(onSuccess, onError, onComplete)

fun Completable.subscribeBy(
        scheduler: Scheduler = io(),
        onComplete: () -> Unit = onCompleteStub,
        onError: (Throwable) -> Unit = onErrorStub
): Disposable = subscribeOn(scheduler).subscribe(onComplete, onError)
