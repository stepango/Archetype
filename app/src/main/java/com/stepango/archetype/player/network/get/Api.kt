package com.stepango.archetype.player.network.get

import com.stepango.archetype.player.data.db.response.feed.Rss
import io.reactivex.Single
import retrofit2.http.GET

interface Api {

    @GET("feed")
    fun feed(): Single<Rss>
}