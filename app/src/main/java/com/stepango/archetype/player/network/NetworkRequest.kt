package com.stepango.archetype.player.network

import com.stepango.archetype.player.di.injector
import com.stepango.archetype.rx.networkScheduler
import io.reactivex.Scheduler
import io.reactivex.Single


abstract class ApiRequest<T : Any>(
        val scheduler: Scheduler = networkScheduler
) {

    val deps: GraphQLRequestDeps = GraphQLRequestDeps()
    val api: Api = deps.api

    protected abstract fun operation(): Single<T>

    /**
     * Creates on observable and starts job
     */
    fun execute(): Single<T> = operation()
            //            .doOnError { (it) { "Error executing request $id" } }
            .subscribeOn(scheduler)
}

class GraphQLRequestDeps {

    val api: Api = injector.apiService()

}