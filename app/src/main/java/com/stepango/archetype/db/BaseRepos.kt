package com.stepango.archetype.db

import io.reactivex.Completable
import io.reactivex.Single

interface PullableKeyValue<in Key : Any, Value : Any> {
    // would like to use varargs but impossible for now because of
    // https://youtrack.jetbrains.com/issue/KT-9495
    /**
     * Sync data between source and repo by pulling data from source.
     * [pull] method implementation should include `save` method call to sync data
     *
     * Empty [keys] indicates that we need to pull all entities.
     * Probably need to add some limits description here or another method/interface
     * (total number of entities, number of pages, etc.)
     */
    fun pull(keys: List<Key> = emptyList()): Completable

    fun pull(key: Key): Completable = throw NotImplementedError()
}

/**
 * Push data to remote receiver that connected to particular repo
 */
interface Pushable<in Value : Any, Result : Any> {
    /**
     * Sync data between source and repo by pushing data to source.
     * [push] method implementation should include `save/pull` method call to sync data
     */
    fun push(value: Value): Single<Result>
}

interface PullableKeyValueRepo<Key : Any, Value : Any> : KeyValueRepo<Key, Value>, PullableKeyValue<Key, Value>
