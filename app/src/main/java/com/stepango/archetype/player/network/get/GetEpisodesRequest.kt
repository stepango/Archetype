package com.stepango.archetype.player.network.get

import com.stepango.archetype.player.data.db.model.EpisodesModel
import com.stepango.archetype.player.data.db.response.feed.Item
import com.stepango.archetype.player.network.ApiRequest
import io.reactivex.Single

class GetEpisodesRequest : ApiRequest<List<EpisodesModel>>() {
    override fun operation(): Single<List<EpisodesModel>> =
            api.feed()
                    .map { it.channel.item.map(::transformResponse) }
}

fun transformResponse(feedItem: Item) = EpisodesModel(
        feedItem.title,
        feedItem.summary,
        feedItem.image.href,
        feedItem.enclosure.url,
        feedItem.content
)

