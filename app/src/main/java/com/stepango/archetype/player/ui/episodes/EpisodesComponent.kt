package com.stepango.archetype.player.ui.episodes

import com.stepango.archetype.player.data.db.model.EpisodesModel
import com.stepango.archetype.player.data.wrappers.EpisodeListItemWrapper
import com.stepango.archetype.player.data.wrappers.EpisodeWrapper
import com.stepango.archetype.player.di.lazyInject
import com.stepango.archetype.rx.filterNonEmpty
import com.stepango.archetype.rx.filterNotEmpty
import io.reactivex.Observable
import io.reactivex.Single

interface EpisodesComponent {
    fun observeEpisodes(): Observable<List<EpisodeListItemWrapper>>
    fun observeEpisode(id: Long): Observable<EpisodeWrapper>
    fun updateEpisodes(): Single<List<EpisodesModel>>
}

class EpisodesComponentImpl : EpisodesComponent {
    private val episodesRepo by lazyInject { episodesRepo() }

    override fun observeEpisodes(): Observable<List<EpisodeListItemWrapper>>
            = episodesRepo.observeAll()
            .filterNotEmpty()
            .map { it.map(::EpisodeListItemWrapper) }

    override fun observeEpisode(id: Long): Observable<EpisodeWrapper>
            = episodesRepo.observe(id)
            .filterNonEmpty()
            .map(::EpisodeWrapper)

    override fun updateEpisodes(): Single<List<EpisodesModel>>
            = episodesRepo.pull()
}