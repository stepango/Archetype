/**
 * Interfaces describe reactive Repository contract. In rxJava2 terms repository should
 * implement following rules:
 *
 * - `save` methods should return `Single<Value>`
 * - `observe` methods should return `Observable<Value>`, for back pressure handling Observable could be converted to Flowable
 * - `remove` methods should return Completable
 */
package com.stepango.archetype.db

import com.stepango.koptional.Optional
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Single value repository that don't accept null as value
 */
interface SingleValueRepo<Value : Any> {
    /**
     * Saves [value] on given [io.reactivex.Scheduler]
     */
    fun save(value: Value): Single<Value>

    /**
     * Removes value on given [io.reactivex.Scheduler] if present
     */
    fun remove(): Completable

    /**
     * @return Observable that contains [Optional.Some] if value is present
     * or [Optional.EMPTY] if value is null
     */
    fun observe(): Observable<Optional<Value>>
}

/**
 * Key-Value repository that don't accept null as a `value` or `key`
 */
interface KeyValueRepo<Key : Any, Value : Any> {
    /**
     * Saves [value] by [key] on given [io.reactivex.Scheduler]
     */
    fun save(key: Key, value: Value): Single<Value>

    /**
     * Give [value] by [key] on given [io.reactivex.Scheduler]
     */
    fun get(key: Key): Single<Value>

    /**
     * Give all [values] on given [io.reactivex.Scheduler]
     */
    fun getAll(): Observable<Value>

    /**
     * Removes value by given [key] if present
     */
    fun remove(key: Key): Completable

    /**
     * @return Observable that contains [Optional.Some] if value is present
     * or [Optional.EMPTY] if value is null by given [key]
     */
    fun observe(key: Key): Observable<Optional<Value>>

    /**
     * Saves given [Map] of objects by it's keys
     * @return map of saved objects
     */
    fun save(data: Map<Key, Value>): Single<Map<Key, Value>>

    /**
     * Remove values by given set of keys, if present
     */
    fun remove(keys: Set<Key>): Completable

    /**
     * Removes all data from current repository
     */
    fun removeAll(): Completable

    /**
     * Observe all objects changes in repository.
     * Object sorting not guarantied here, use `.map { sort(it) }` or custom interface extension
     * to achieve desired sorting order
     */
    fun observeAll(): Observable<List<Value>>
}

/**
 * Key-ListOfValues repository
 */
interface KeyValueListRepo<in Key : Any, Value : Any> {
    /**
     * Saves [value] by [key] on given [io.reactivex.Scheduler]
     */
    fun save(key: Key, value: List<Value>): Single<List<Value>>

    /**
     * Removes value by given [key] if present
     */
    fun remove(key: Key): Completable

    /**
     * @return Observable that contains non empty list if value is present
     * or [emptyList] if no values is stored by given [key]
     */
    fun observe(key: Key): Observable<List<Value>>

    /**
     * Removes all data from current repository
     */
    fun removeAll(): Completable
}