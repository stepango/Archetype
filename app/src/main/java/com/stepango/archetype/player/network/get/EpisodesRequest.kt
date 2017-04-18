package com.stepango.archetype.player.network.get

import com.stepango.archetype.player.data.db.model.EpisodesModel
import com.stepango.archetype.player.data.db.response.feed.Item
import com.stepango.archetype.player.di.lazyInject
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers.io

class EpisodesRequest {

    val api by lazyInject { apiService() }

    fun operation(): Single<List<EpisodesModel>> = api.feed()
            .map { it.channel.item.map { feedItem -> transformResponse(feedItem) } }
            .subscribeOn(io())

    private fun transformResponse(feedItem: Item)
            = EpisodesModel(
            feedItem.title,
            feedItem.summary,
            feedItem.image.href,
            feedItem.enclosure.url,
            feedItem.content)

}

