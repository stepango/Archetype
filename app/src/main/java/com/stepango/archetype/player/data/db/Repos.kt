package com.stepango.archetype.player.data.db

import com.stepango.archetype.R
import com.stepango.archetype.action.argsOf
import com.stepango.archetype.db.PullableKeyValueRepo
import com.stepango.archetype.player.data.db.model.EpisodesModel
import com.stepango.archetype.player.di.injector
import io.reactivex.Completable

interface EpisodesModelRepo : PullableKeyValueRepo<Long, EpisodesModel> {

    override fun pull(keys: List<Long>): Completable = injector.run {
        apiActionsProducer()
                .createAction(R.id.action_get_episodes)
                .invoke(apiService(), argsOf())
    }

}
