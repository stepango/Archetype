package com.stepango.archetype.player.db

import com.stepango.archetype.db.PullableKeyValueRepo

import com.stepango.archetype.player.db.model.EpisodesModel
import com.stepango.archetype.player.network.get.EpisodesRequest
import io.reactivex.Single

interface EpisodesModelRepo : PullableKeyValueRepo<Long, EpisodesModel> {
    override fun pull(keys: List<Long>): Single<List<EpisodesModel>> = EpisodesRequest()
            .operation()
            .flatMap { save(it.associateBy({ it.id }, { it })) }
            .map { it.values.toList() }
}
