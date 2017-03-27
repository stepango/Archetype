package com.stepango.archetype.player.network.get

import com.stepango.archetype.player.db.model.EpisodesModel
import com.stepango.archetype.player.di.lazyInject
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers.io

class EpisodesRequest {

    val api by lazyInject { apiService() }

    fun operation(): Single<List<EpisodesModel>> = api.feed()
            .map { it.channel.item.map { feedItem -> EpisodesModel(feedItem.title) } }
            .subscribeOn(io())

}

