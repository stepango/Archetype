package com.stepango.archetype.rx

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction

private fun <T> Iterator<T>.toIterable() = object : Iterable<T> {
    override fun iterator(): Iterator<T> = this@toIterable
}

fun BooleanArray.toObservable(): Observable<Boolean> = this.toList().toObservable()
fun ByteArray.toObservable(): Observable<Byte> = this.toList().toObservable()
fun ShortArray.toObservable(): Observable<Short> = this.toList().toObservable()
fun IntArray.toObservable(): Observable<Int> = this.toList().toObservable()
fun LongArray.toObservable(): Observable<Long> = this.toList().toObservable()
fun FloatArray.toObservable(): Observable<Float> = this.toList().toObservable()
fun DoubleArray.toObservable(): Observable<Double> = this.toList().toObservable()
fun <T> Array<out T>.toObservable(): Observable<T> = Observable.fromArray(*this)

fun IntProgression.toObservable(): Observable<Int> =
        if (step == 1 && last.toLong() - first < Integer.MAX_VALUE) Observable.range(first, Math.max(0, last - first + 1))
        else Observable.fromIterable(this)

fun <T> Iterator<T>.toObservable(): Observable<T> = toIterable().toObservable()
fun <T> Iterable<T>.toObservable(): Observable<T> = Observable.fromIterable(this)
fun <T> Sequence<T>.toObservable(): Observable<T> = Observable.fromIterable(object : Iterable<T> {
    override fun iterator(): Iterator<T> = this@toObservable.iterator()
})

fun <T> Iterable<Observable<out T>>.merge(): Observable<T> = Observable.merge(this.toObservable())
fun <T> Iterable<Observable<out T>>.mergeDelayError(): Observable<T> = Observable.mergeDelayError(this.toObservable())

inline fun <T1, T2, R> zip(source: Observable<T1>, target: Observable<T2>, crossinline block: (T1, T2) -> R): Observable<R>
        = Observable.zip<T1, T2, R>(source, target, BiFunction { t1, t2 -> block(t1, t2) })

inline fun <T1, T2, R> combineLatest(source: Observable<T1>, target: Observable<T2>, crossinline block: (T1, T2) -> R): Observable<R>
        = Observable.combineLatest<T1, T2, R>(source, target, BiFunction { t1, t2 -> block(t1, t2) })

fun <T, R> Observable<T>.fold(initial: R, body: (R, T) -> R): Single<R> = reduce(initial, { a, e -> body(a, e) })
fun <T> Observable<T>.onError(block: (Throwable) -> Unit): Observable<T> = doOnError(block)

/**
 * Returns Observable that emits objects from kotlin [Sequence] returned by function you provided by parameter [body] for
 * each input object and merges all produced elements into one observable.
 * Works similar to [Observable.flatMap] and [Observable.flatMapIterable] but with [Sequence]
 *
 * @param body is a function that applied for each item emitted by source observable that returns [Sequence]
 * @returns Observable that merges all [Sequence]s produced by [body] functions
 */
fun <T, R> Observable<T>.flatMapSequence(body: (T) -> Sequence<R>): Observable<R> = flatMap { body(it).toObservable() }

fun <T> Observable<Observable<T>>.switchOnNext(): Observable<T> = Observable.switchOnNext(this)


/**
 * Returns an Observable that emits the items emitted by the source Observable, converted to the specified resourceType.
 */
inline fun <reified R : Any> Observable<*>.cast(): Observable<R> = cast(R::class.java)