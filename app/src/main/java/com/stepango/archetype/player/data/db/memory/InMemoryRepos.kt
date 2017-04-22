package com.stepango.archetype.player.data.db.memory

import com.stepango.archetype.action.ActionProducer
import com.stepango.archetype.action.ApiAction
import com.stepango.archetype.db.KeyValueRepo
import com.stepango.archetype.player.data.db.EpisodesModelRepo
import com.stepango.archetype.player.data.db.model.EpisodesModel
import com.stepango.archetype.player.network.Api

class InMemoryEpisodesRepo(
        override val actionProducer: ActionProducer<ApiAction>,
        override val apiService: Api
) :
        EpisodesModelRepo,
        KeyValueRepo<Long, EpisodesModel> by InMemoryKeyValueRepo<Long, EpisodesModel>()