package com.stepango.archetype.player.di

import android.content.Context
import android.util.SparseArray
import com.ninetyseconds.auckland.core.log.SimpleLogger
import com.stepango.archetype.App
import com.stepango.archetype.BuildConfig
import com.stepango.archetype.R
import com.stepango.archetype.action.ActionProducer
import com.stepango.archetype.action.ActionHandler
import com.stepango.archetype.action.IntentMaker
import com.stepango.archetype.action.IntentMakerImpl
import com.stepango.archetype.action.IdleAction
import com.stepango.archetype.action.ApiAction
import com.stepango.archetype.action.ApiActionHandler
import com.stepango.archetype.action.ApiActionProducer
import com.stepango.archetype.action.ContextAction
import com.stepango.archetype.action.ContextActionProducer
import com.stepango.archetype.action.ContextActionHandler
import com.stepango.archetype.glide.GlideImageLoader
import com.stepango.archetype.image.ImageLoader
import com.stepango.archetype.logger.Logger
import com.stepango.archetype.player.data.db.EpisodesModelRepo
import com.stepango.archetype.player.data.db.memory.InMemoryEpisodesRepo
import com.stepango.archetype.player.loader.CancelDownloadEpisodeAction
import com.stepango.archetype.player.loader.DownloadEpisodeAction
import com.stepango.archetype.player.loader.RefreshDownloadedFilesAction
import com.stepango.archetype.player.network.Api
import com.stepango.archetype.player.network.get.BASE_URL
import com.stepango.archetype.player.network.get.GetEpisodesAction
import com.stepango.archetype.player.network.get.WEB_SERVICE_TIMEOUT
import com.stepango.archetype.player.ui.additional.MockToaster
import com.stepango.archetype.player.ui.episodes.EpisodesComponent
import com.stepango.archetype.player.ui.episodes.EpisodesComponentImpl
import com.stepango.archetype.player.ui.player.PlayerComponent
import com.stepango.archetype.player.ui.player.PlayerComponentImpl
import com.stepango.archetype.player.ui.player.ShowEpisodeAction
import com.stepango.archetype.ui.Toaster
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit

val Any.injector: Injector by lazy { InjectorImpl(App.instance) }

inline fun <T> lazyInject(crossinline block: Injector.() -> T): Lazy<T> = lazy { Injector().block() }

interface Injector {
    fun logger(): Logger

    fun contextActionsHandler(): ActionHandler<Context>
    fun contextActionsProducer(): ActionProducer<ContextAction>
    fun intentMaker(): IntentMaker

    //region ui
    fun imageLoader(): ImageLoader

    fun toaster(): Toaster
    //endregion

    //region media
    fun player(): PlayerComponent
    //endregion

    //region repositories
    fun episodesRepo(): EpisodesModelRepo
    //endregion

    //region components
    fun episodesComponent(): EpisodesComponent
    //endregion

    //region network
    fun apiService(): Api

    fun apiActionsHandler(): ActionHandler<Api>
    fun apiActionsProducer(): ActionProducer<ApiAction>
    //endregion


    companion object {
        operator fun invoke(): Injector = injector
    }
}

class InjectorImpl(val app: App) : Injector {
    override fun apiActionsHandler(): ActionHandler<Api> = ApiActionHandler(apiActionsProducer())

    override fun apiActionsProducer(): ActionProducer<ApiAction> = ApiActionProducer(apiService(), apiActions)

    private object logger : Logger by SimpleLogger()

    override fun logger(): Logger = logger

    override fun contextActionsHandler() = ContextActionHandler(contextActionsProducer())

    override fun contextActionsProducer(): ActionProducer<@JvmSuppressWildcards ContextAction> = ContextActionProducer(app, contextActions)

    override fun intentMaker() = IntentMakerImpl()

    //region media
    override fun player() = PlayerComponentImpl(app)
    //endregion

    //region ui
    override fun imageLoader() = GlideImageLoader()

    //TODO: replace by SimpleToaster with context
    override fun toaster(): Toaster = MockToaster()
    //endregion

    //region repositories
    private val episodesRepo: EpisodesModelRepo by lazy { InMemoryEpisodesRepo(
            apiActionsProducer(), apiService(),
            contextActionsProducer(), app) }

    override fun episodesRepo(): EpisodesModelRepo = episodesRepo
    //endregion

    //region components
    override fun episodesComponent(): EpisodesComponent = EpisodesComponentImpl()
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
    //endregion
}

val contextActions = SparseArray<ContextAction>().apply {
    put(R.id.action_idle, IdleAction())
    put(R.id.action_show_episode, ShowEpisodeAction())
    put(R.id.action_download_episode, DownloadEpisodeAction())
    put(R.id.action_cancel_download_episode, CancelDownloadEpisodeAction())
    put(R.id.action_refresh_episode_files, RefreshDownloadedFilesAction())
}

val apiActions = SparseArray<ApiAction>().apply {
    put(R.id.action_get_episodes, GetEpisodesAction())
}
