package com.stepango.archetype.player.ui.episodes

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.stepango.archetype.player.data.db.EpisodesModelRepo
import com.stepango.koptional.Optional
import io.reactivex.Completable
import io.reactivex.Observable
import org.junit.Test

class EpisodesUseCaseImplTest {
    @Test
    fun observeEpisodes() {
        val episodesRepo = mock<EpisodesModelRepo> {
            on { observeAll() } doReturn Observable.just(emptyList())
        }
        val component = EpisodesUseCaseImpl(episodesRepo)
        component.observeEpisodes()
                .test()
                .assertValueCount(0)
    }

    @Test
    fun observeEpisode() {
        val episodesRepo = mock<EpisodesModelRepo> {
            on { observe(any()) } doReturn Observable.just(Optional.empty())
        }
        val component = EpisodesUseCaseImpl(episodesRepo)
        component.observeEpisode(0)
                .test()
                .assertValueCount(0)
    }

    @Test
    fun updateEpisodes() {
        val episodesRepo = mock<EpisodesModelRepo> {
            on { pull() } doReturn Completable.complete()
        }
        val component = EpisodesUseCaseImpl(episodesRepo)
        component.updateEpisodes()
                .test()
                .assertComplete()
    }

    @Test
    fun observeEpisodesNonEmpty() {
        val episodesRepo = mock<EpisodesModelRepo> {
            on { observeAll() } doReturn Observable.just(listOf(mock()))
        }
        val component = EpisodesUseCaseImpl(episodesRepo)
        component.observeEpisodes()
                .test()
                .assertValueCount(1)

        verify(episodesRepo, times(1)).observeAll()
    }

}