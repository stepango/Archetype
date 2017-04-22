package com.stepango.archetype

import com.stepango.archetype.player.data.db.response.feed.Rss
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
    fun testResponseParser() {
        server.enqueue(MockResponse().setBody(getStringFromResources("feed_response.xml")))

        val call = service!!.get()
        val response = call.execute()
        val body = response.body()
        Assert.assertEquals("Android Dev Подкаст", body.channel.title)
        val item = body.channel.item[0]
        Assert.assertEquals("My Title", item.title)
        Assert.assertEquals("My Summary", item.summary)
        Assert.assertEquals("My Content", item.content)
        Assert.assertEquals("https://my.url", item.enclosure.url)
        Assert.assertEquals("my/type", item.enclosure.type)
        Assert.assertEquals("http://image.png", item.image.href)
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