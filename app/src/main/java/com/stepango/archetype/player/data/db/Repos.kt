package com.stepango.archetype.player.data.db

import com.stepango.archetype.action.ContextActionHandler
import com.stepango.archetype.db.KeyValueRepo
import com.stepango.archetype.player.data.db.model.EpisodesModel
import io.reactivex.Completable

interface EpisodesModelRepo : KeyValueRepo<Long, EpisodesModel> {

    fun pull(): Completable

    fun refreshFiles(ah: ContextActionHandler): Completable
}

