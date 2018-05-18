package com.stepango.archetype.player.ui.episodes

import com.stepango.archetype.action.ContextActionHandler
import com.stepango.archetype.player.data.db.EpisodesModelRepo
import com.stepango.archetype.player.data.db.model.EpisodesModel
import com.stepango.archetype.player.data.wrappers.EpisodeWrapper
import com.stepango.archetype.rx.filterNonEmpty
import com.stepango.archetype.rx.filterNotEmpty
import io.reactivex.Completable
import io.reactivex.Observable

interface EpisodesUseCase {
    fun observeEpisodes(): Observable<List<EpisodesModel>>
    fun observeEpisode(id: Long): Observable<EpisodesModel>
    fun updateEpisodes(ah: ContextActionHandler): Completable
}

class EpisodesUseCaseImpl(
        private val episodesRepo: EpisodesModelRepo
) : EpisodesUseCase {

    override fun observeEpisodes(): Observable<List<EpisodesModel>> = episodesRepo.observeAll()
            .filterNotEmpty()

    override fun observeEpisode(id: Long): Observable<EpisodesModel> = episodesRepo.observe(id)
            .filterNonEmpty()

    override fun updateEpisodes(ah: ContextActionHandler): Completable =
            episodesRepo.pull()
                    .andThen(episodesRepo.refreshFiles(ah))
}