package com.stepango.archetype.player.db.memory

import android.databinding.ObservableArrayMap
import android.databinding.ObservableMap.OnMapChangedCallback
import com.stepango.archetype.db.KeyValueRepo
import com.stepango.koptional.Optional
import com.stepango.koptional.toOptional
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import kotlin.reflect.KClass

inline fun <Key : Any, reified Value : Any> InMemoryKeyValueRepo(): InMemoryKeyValueRepo<Key, Value> = InMemoryKeyValueRepo(Value::class)

class InMemoryKeyValueRepo<Key : Any, Value : Any>(val valClass: KClass<Value>) : KeyValueRepo<Key, Value> {

    val map = ObservableArrayMap<Key, Value>()
    val publisher: PublishSubject<Map<Key, Value>> = PublishSubject.create()

    override fun save(key: Key, value: Value): Single<Value> = Single.just(value).doOnSuccess {
        map[key] = value
        triggerObserveAllNotification()
    }

    override fun remove(key: Key): Completable = Completable.fromAction {
        map.remove(key)
        triggerObserveAllNotification()
    }

    override fun observe(key: Key): Observable<Optional<Value>> = Observable.create { s ->
        val observer = object : OnMapChangedCallback<ObservableArrayMap<Key, Value>, Key, Value>() {
            override fun onMapChanged(sender: ObservableArrayMap<Key, Value>, updatedKey: Key) {
                try {
                    if (key == updatedKey) s.onNext(sender[key].toOptional())
                } catch (e: Exception) {
                    s.onError(e)
                }
            }
        }
        map.addOnMapChangedCallback(observer)
        s.setCancellable { map.removeOnMapChangedCallback(observer) }
    }

    override fun save(data: Map<Key, Value>): Single<Map<Key, Value>> {
        map.putAll(data)
        triggerObserveAllNotification()
        return Single.just(map)
    }

    private fun triggerObserveAllNotification() {
        publisher.onNext(map)
    }

    override fun remove(keys: Set<Key>): Completable = Completable.fromAction {
        keys.forEach { map.remove(it) }
        triggerObserveAllNotification()
    }

    override fun removeAll(): Completable = Completable.fromAction {
        map.clear()
        triggerObserveAllNotification()
    }

    override fun observeAll(): Observable<List<Value>> = publisher
            .map { it.values.toList() }
}