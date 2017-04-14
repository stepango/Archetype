package com.stepango.archetype.player.data.db.memory

import android.databinding.ObservableArrayMap
import android.databinding.ObservableMap.OnMapChangedCallback
import com.stepango.archetype.db.KeyValueRepo
import com.stepango.koptional.Optional
import com.stepango.koptional.toOptional
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import kotlin.reflect.KClass

inline fun <Key : Any, reified Value : Any> InMemoryKeyValueRepo(): InMemoryKeyValueRepo<Key, Value> = InMemoryKeyValueRepo(Value::class)

class InMemoryKeyValueRepo<Key : Any, Value : Any>(val valClass: KClass<Value>) : KeyValueRepo<Key, Value> {


    override fun save(key: Key, value: Value): Single<Value> = Single.just(value).doOnSuccess {
        map[key] = value
        triggerObserveAllNotification()
    }

    val map = ObservableArrayMap<Key, Value>()
    val publisher: BehaviorSubject<Map<Key, Value>> = BehaviorSubject.create()

    override fun save(data: Map<Key, Value>): Single<Map<Key, Value>> {
        map.putAll(data)
        triggerObserveAllNotification()
        return Single.just(map)
    }

    override fun removeAll(): Completable = Completable.fromAction {
        map.clear()
        triggerObserveAllNotification()
    }

    private fun triggerObserveAllNotification() {
        publisher.onNext(map)
    }

    override fun observeAll(): Observable<List<Value>> = publisher
            .map { it.values.toList() }


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
        map[key]?.let { s.onNext(it.toOptional()) }
    }



    override fun remove(keys: Set<Key>): Completable = Completable.fromAction {
        keys.forEach { map.remove(it) }
        triggerObserveAllNotification()
    }


}