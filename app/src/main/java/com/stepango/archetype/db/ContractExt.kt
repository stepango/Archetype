package com.stepango.archetype.db

import com.stepango.archetype.rx.filterNonEmpty
import io.reactivex.Single

fun <Key : Any, Value : Any> KeyValueRepo<Key, Value>.single(key: Key): Single<Value>
        = observe(key).filterNonEmpty().firstOrError()

fun <Value : Any> SingleValueRepo<Value>.single(): Single<Value>
        = observe().filterNonEmpty().firstOrError()