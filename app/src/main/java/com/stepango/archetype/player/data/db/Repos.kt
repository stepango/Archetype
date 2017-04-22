package com.stepango.archetype.player.data.db

import com.stepango.archetype.R
import com.stepango.archetype.action.ActionProducer
import com.stepango.archetype.action.ApiAction
import com.stepango.archetype.action.argsOf
import com.stepango.archetype.db.KeyValueRepo
import com.stepango.archetype.player.data.db.model.EpisodesModel
import com.stepango.archetype.player.network.Api
import io.reactivex.Completable

interface EpisodesModelRepo : KeyValueRepo<Long, EpisodesModel> {

    val actionProducer: ActionProducer<ApiAction>
    val apiService: Api

    fun pull(): Completable = actionProducer
            .createAction(R.id.action_get_episodes)
            .invoke(apiService, argsOf())
}

