package com.stepango.archetype.player.network

import com.stepango.archetype.player.data.db.response.feed.Rss
import io.reactivex.Single
import retrofit2.http.GET

interface Api {

    @GET("Podcast/android.xml")
    fun feed(): Single<Rss>
}