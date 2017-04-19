package com.stepango.archetype.player.network.get

import com.stepango.archetype.action.ApiAction
import com.stepango.archetype.action.Args
import com.stepango.archetype.player.data.db.model.EpisodesModel
import com.stepango.archetype.player.data.db.response.feed.Item
import com.stepango.archetype.player.di.lazyInject
import com.stepango.archetype.player.network.Api
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers.io

class GetEpisodesAction : ApiAction {

    val episodesRepo by lazyInject { episodesRepo() }

    override fun invoke(context: Api, args: Args): Completable = context.feed()
                    .map { it.channel.item.map(::transformResponse) }
                    .flatMap { episodesRepo.save(it.associateBy({ it.id }) { it }) }
                    .subscribeOn(io())
                    .toCompletable()

}

fun transformResponse(feedItem: Item) = EpisodesModel(
        feedItem.title,
        feedItem.summary,
        feedItem.image.href,
        feedItem.enclosure.url,
        feedItem.content
)

