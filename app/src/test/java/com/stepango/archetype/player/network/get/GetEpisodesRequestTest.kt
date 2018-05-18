package com.stepango.archetype.player.network.get

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.stepango.archetype.action.argsOf
import com.stepango.archetype.player.data.db.EpisodesModelRepo
import com.stepango.archetype.player.data.db.model.EpisodesModel
import com.stepango.archetype.player.data.db.response.feed.Channel
import com.stepango.archetype.player.data.db.response.feed.Enclosure
import com.stepango.archetype.player.data.db.response.feed.Image
import com.stepango.archetype.player.data.db.response.feed.Item
import com.stepango.archetype.player.data.db.response.feed.Rss
import com.stepango.archetype.player.di.injector
import com.stepango.archetype.player.network.Api
import io.reactivex.Single
import org.junit.Test

class GetEpisodesRequestTest {
    @Test
    fun invoke() {

        val repo = mock<EpisodesModelRepo> {
            on { save(any()) } doReturn Single.just(emptyMap<Long, EpisodesModel>())
        }
        val context = mock<Api> {
            on { feed() } doReturn Single.just(rssStub())
        }
        whenever(injector.episodesRepo()).doReturn(repo)

        GetEpisodesRequest().execute(context, argsOf())
                .test()
                .await()
                .assertComplete()

        verify(repo, times(1)).save(any())
    }

    private fun rssStub(): Rss {
        return Rss().apply {
            channel = Channel().apply {
                item = listOf(Item().apply {
                    title = "Title"
                    content = "Content"
                    summary = "Summary"
                    enclosure = Enclosure().apply {
                        url = "url"
                        type = "type"
                    }
                    image = Image().apply {
                        href = "href"
                    }
                })
            }
        }
    }
}