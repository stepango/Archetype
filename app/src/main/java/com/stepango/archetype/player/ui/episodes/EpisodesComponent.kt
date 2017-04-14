package com.stepango.archetype.player.ui.episodes

import com.stepango.archetype.player.data.db.model.EpisodesModel
import com.stepango.archetype.player.data.wrappers.EpisodesWrapper
import com.stepango.archetype.player.di.lazyInject
import com.stepango.archetype.rx.filterNotEmpty
import io.reactivex.Observable
import io.reactivex.Single

interface EpisodesComponent {
    fun observeEpisodes(): Observable<List<EpisodesWrapper>>
    fun updateEpisodes(): Single<List<EpisodesModel>>
}

class EpisodesComponentImpl : EpisodesComponent {

    private val episodesRepo by lazyInject { episodesRepo() }

    override fun observeEpisodes(): Observable<List<EpisodesWrapper>>
            = episodesRepo.observeAll()
            .filterNotEmpty()
            .map { it.map(::EpisodesWrapper) }

    override fun updateEpisodes(): Single<List<EpisodesModel>>
            = episodesRepo.pull()
}