package com.stepango.archetype.player.network.get

import com.stepango.archetype.player.db.model.EpisodesModel
import io.reactivex.Single

class EpisodesRequest {
    fun operation(): Single<List<EpisodesModel>> = Single.just(listOf(
            EpisodesModel(0, "aaa"),
            EpisodesModel(1, "aab"),
            EpisodesModel(2, "abb"),
            EpisodesModel(3, "bbb"),
            EpisodesModel(4, "bbc")
    ))
}

