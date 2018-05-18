package com.stepango.archetype.player.data.db.memory

import com.stepango.archetype.action.ContextActionHandler
import com.stepango.archetype.db.KeyValueRepo
import com.stepango.archetype.player.data.db.EpisodesModelRepo
import com.stepango.archetype.player.data.db.model.EpisodesModel
import com.stepango.archetype.player.loader.RefreshDownloadedAction
import com.stepango.archetype.player.network.get.GetEpisodesRequest
import io.reactivex.Completable

class InMemoryEpisodesRepo(
        val refreshDownloadedAction: RefreshDownloadedAction
) :
        EpisodesModelRepo,
        KeyValueRepo<Long, EpisodesModel> by InMemoryKeyValueRepo() {
    override fun pull(): Completable =
            GetEpisodesRequest().execute()
                    .flatMap {
                        save(it.associateBy({ it.id }) { it })
                    }
                    .toCompletable()

    override fun refreshFiles(ah: ContextActionHandler) = ah.createAction(refreshDownloadedAction, Unit)
}