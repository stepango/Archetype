package com.stepango.archetype

import com.stepango.archetype.player.db.response.feed.Rss
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.stream.Format
import org.simpleframework.xml.stream.HyphenStyle
import org.simpleframework.xml.stream.Verbosity
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.GET
import java.io.IOException


class FeedResponseParsingTest {

    internal interface Service {
        @GET("/")
        fun get(): Call<Rss>
    }

    @Rule @JvmField
    val server = MockWebServer()

    private var service: Service? = null

    @Before
    fun setUp() {
        val service = mockService
        this.service = service
    }

    private val mockService: Service
        get() {
            val format = Format(0, null, HyphenStyle(), Verbosity.HIGH)
            val persister = Persister(format)
            val retrofit = Retrofit.Builder()
                    .baseUrl(server.url("/"))
                    .addConverterFactory(SimpleXmlConverterFactory.create(persister))
                    .build()
            return retrofit.create(Service::class.java)
        }

    @Test
    @Throws(IOException::class)
    fun testExchangeRatesResponseParser() {
        server.enqueue(MockResponse().setBody(getStringFromResources("feed_response.xml")))

        val call = service!!.get()
        val response = call.execute()
        val body = response.body()
        Assert.assertEquals("Подкасты Android Dev", body.channel.title)
        Assert.assertEquals("Интересные материалы для Android-разработчика #63", body.channel.item[0].title)
    }

    @Throws(IOException::class)
    private fun getStringFromResources(filePath: String): String {
        val `is` = this.javaClass.classLoader.getResourceAsStream(filePath)
        val s = java.util.Scanner(`is`).useDelimiter("\\A")
        val result = if (s.hasNext()) s.next() else ""
        `is`.close()
        return result
    }

}