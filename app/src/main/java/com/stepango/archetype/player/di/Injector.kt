package com.stepango.archetype.player.di

import android.util.SparseArray
import com.ninetyseconds.auckland.core.log.SimpleLogger
import com.stepango.archetype.BuildConfig
import com.stepango.archetype.R
import com.stepango.archetype.action.ActionHandler
import com.stepango.archetype.action.ContextAction
import com.stepango.archetype.action.MainActionHandler
import com.stepango.archetype.glide.GlideImageLoader
import com.stepango.archetype.image.ImageLoader
import com.stepango.archetype.logger.Logger
import com.stepango.archetype.player.actions.IdleAction
import com.stepango.archetype.player.db.EpisodesModelRepo
import com.stepango.archetype.player.db.memory.InMemoryEpisodesRepo
import com.stepango.archetype.player.network.get.Api
import com.stepango.archetype.player.network.get.BASE_URL
import com.stepango.archetype.player.network.get.WEB_SERVICE_TIMEOUT
import com.stepango.archetype.player.ui.additional.MockToaster
import com.stepango.archetype.ui.Toaster
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit


val Any.injector: Injector by lazy { InjectorImpl() }

inline fun <T> lazyInject(crossinline block: Injector.() -> T): Lazy<T> = lazy { Injector().block() }

interface Injector {
    fun logger(): Logger
    fun mainActionHandler(): ActionHandler
    fun imageLoader(): ImageLoader
    fun toaster(): Toaster

    //region repositories
    fun episodesRepo(): EpisodesModelRepo
    //endregion

    //region network
    fun apiService(): Api
    //endregion

    companion object {
        operator fun invoke(): Injector = injector
    }
}

class InjectorImpl : Injector {

    private object logger : Logger by SimpleLogger()

    override fun logger(): Logger = logger

    override fun mainActionHandler(): ActionHandler = MainActionHandler(actions)

    override fun imageLoader() = GlideImageLoader()

    //TODO: replace by SimpleToaster with context
    override fun toaster(): Toaster = MockToaster()

    //region repositories
    override fun episodesRepo() = InMemoryEpisodesRepo()
    //endregion

    //region network

    override fun apiService(): Api = service

    private var okHttpClient: OkHttpClient = httpClient()

    private fun httpClient(): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
                .readTimeout(WEB_SERVICE_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(WEB_SERVICE_TIMEOUT, TimeUnit.MILLISECONDS)
        if (BuildConfig.DEBUG) {
            okHttpClientBuilder.interceptors().add(logInterceptor())
        }
        return okHttpClientBuilder.build()
    }

    private val restAdapter = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    private var service: Api = restAdapter.create(Api::class.java)

    private fun logInterceptor(): Interceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    //enregion
}

val actions = SparseArray<ContextAction>().apply {
    put(R.id.action_idle, IdleAction())
}
