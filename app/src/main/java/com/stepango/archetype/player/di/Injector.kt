package com.stepango.archetype.player.di

import android.content.Context
import com.stepango.archetype.App
import com.stepango.archetype.BuildConfig
import com.stepango.archetype.action.ContextActionHandler
import com.stepango.archetype.action.ContextActionHandlerRealFactory
import com.stepango.archetype.action.IntentMaker
import com.stepango.archetype.action.IntentMakerImpl
import com.stepango.archetype.action.argsOf
import com.stepango.archetype.fragment.BaseFragment
import com.stepango.archetype.glide.GlideImageLoader
import com.stepango.archetype.image.ImageLoader
import com.stepango.archetype.logger.Logger
import com.stepango.archetype.logger.SimpleLogger
import com.stepango.archetype.player.data.db.EpisodesModelRepo
import com.stepango.archetype.player.data.db.memory.InMemoryEpisodesRepo
import com.stepango.archetype.player.data.wrappers.EpisodeItemWrapperFabric
import com.stepango.archetype.player.data.wrappers.EpisodeItemWrapperFabricImpl
import com.stepango.archetype.player.data.wrappers.EpisodeListItemWrapperFabric
import com.stepango.archetype.player.data.wrappers.EpisodeListItemWrapperFabricImpl
import com.stepango.archetype.player.loader.CancelDownloadEpisodeAction
import com.stepango.archetype.player.loader.CancelDownloadEpisodeActionImpl
import com.stepango.archetype.player.loader.DownloadEpisodeAction
import com.stepango.archetype.player.loader.DownloadEpisodeActionImpl
import com.stepango.archetype.player.loader.RefreshDownloadedFilesActionImpl
import com.stepango.archetype.player.network.Api
import com.stepango.archetype.player.network.get.BASE_URL
import com.stepango.archetype.player.network.get.WEB_SERVICE_TIMEOUT
import com.stepango.archetype.player.ui.additional.MockToaster
import com.stepango.archetype.player.ui.episodes.EpisodesUseCase
import com.stepango.archetype.player.ui.episodes.EpisodesUseCaseImpl
import com.stepango.archetype.player.ui.player.PlayerComponent
import com.stepango.archetype.player.ui.player.PlayerComponentImpl
import com.stepango.archetype.player.ui.player.ShowEpisodeActionImpl
import com.stepango.archetype.rx.CompositeDisposableHolder
import com.stepango.archetype.rx.CompositeDisposableHolderImpl
import com.stepango.archetype.ui.Toaster
import com.stepango.archetype.viewmodel.LoaderHolder
import com.stepango.archetype.viewmodel.LoaderHolderImpl
import com.stepango.archetype.viewmodel.LoadingProgressHelper
import com.stepango.archetype.viewmodel.LoadingProgressHelperImpl
import com.stepango.archetype.viewmodel.RxLifecycle
import com.stepango.archetype.viewmodel.RxLifecycleImpl
import com.stepango.archetype.viewmodel.ViewModel
import com.stepango.archetype.viewmodel.ViewModelImpl
import com.trello.navi2.Event
import com.trello.navi2.NaviComponent
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

    fun contextActionHandler(context: Context, compositeDisposableHolder: CompositeDisposableHolder): ContextActionHandler

    fun compositeDisposableHolder(): CompositeDisposableHolder = CompositeDisposableHolderImpl()

    fun intentMaker(): IntentMaker

    //region ui
    fun imageLoader(): ImageLoader

    fun toaster(): Toaster

    fun loaderHolder(): LoaderHolder = LoaderHolderImpl()

    fun loadingProgressHelper(): LoadingProgressHelper = LoadingProgressHelperImpl()
    //endregion

    //region media
    fun player(): PlayerComponent
    //endregion

    //region repositories
    fun episodesRepo(): EpisodesModelRepo
    //endregion

    //region usecase
    fun episodesUseCase(): EpisodesUseCase
    //endregion

    //region wrapper
    fun episodeListItemWrapperFabric(contextActionHandler: ContextActionHandler): EpisodeListItemWrapperFabric

    fun episodeItemWrapperFabric(contextActionHandler: ContextActionHandler): EpisodeItemWrapperFabric
    //endregion

    //region network
    fun apiService(): Api

    //endregion

    //region fragment

    fun rxlc(naviComponent: NaviComponent, compositeDisposableHolder: CompositeDisposableHolder): RxLifecycle

    fun vm(fragment: BaseFragment<*>): ViewModel

    //endregion


    companion

    object {
        operator fun invoke(): Injector = injector
    }
}

class InjectorImpl(val app: App) : Injector {

    private object logger : Logger by SimpleLogger()

    private val actionHandlerfactory by lazy { ContextActionHandlerRealFactory() }

    override fun logger(): Logger = logger

    override fun contextActionHandler(context: Context, compositeDisposableHolder: CompositeDisposableHolder): ContextActionHandler =
            actionHandlerfactory.createActionHandler(context, compositeDisposableHolder)

    override fun intentMaker() = IntentMakerImpl()

    //region media
    override fun player() = PlayerComponentImpl(app)
    //endregion

    //region ui
    override fun imageLoader() = GlideImageLoader()

    //TODO: replace by SimpleToaster with context
    override fun toaster(): Toaster = MockToaster()
    //endregion

    //region actions
    private fun refreshDownloadedFilesAction() = RefreshDownloadedFilesActionImpl()

    private fun showEpisodeAction() = ShowEpisodeActionImpl()
    private fun cancelDownloadEpisodeAction(): CancelDownloadEpisodeAction = CancelDownloadEpisodeActionImpl()
    private fun downloadEpisodeAction(): DownloadEpisodeAction = DownloadEpisodeActionImpl(intentMaker())
    //endregion

    //region repositories

    private val episodesRepo: EpisodesModelRepo by lazy {
        InMemoryEpisodesRepo(refreshDownloadedFilesAction())
    }

    override fun episodesRepo(): EpisodesModelRepo = episodesRepo
    //endregion

    //region usecase
    override fun episodesUseCase(): EpisodesUseCase = EpisodesUseCaseImpl(episodesRepo())
    //endregion

    //region wrappers
    override fun episodeListItemWrapperFabric(contextActionHandler: ContextActionHandler): EpisodeListItemWrapperFabric =
            EpisodeListItemWrapperFabricImpl(contextActionHandler, showEpisodeAction(), cancelDownloadEpisodeAction(), downloadEpisodeAction())

    override fun episodeItemWrapperFabric(contextActionHandler: ContextActionHandler): EpisodeItemWrapperFabric =
            EpisodeItemWrapperFabricImpl(contextActionHandler, cancelDownloadEpisodeAction(), downloadEpisodeAction())
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

    //region fragment

    override fun rxlc(
            naviComponent: NaviComponent,
            compositeDisposableHolder: CompositeDisposableHolder
    ): RxLifecycle = RxLifecycleImpl(
            naviComponent = naviComponent,
            compositeDisposableHolder = compositeDisposableHolder,
            startEvent = Event.START,
            stopEvent = Event.STOP,
            detachEvent = Event.DETACH
    )

    override fun vm(fragment: BaseFragment<*>): ViewModel = compositeDisposableHolder().let { compositeDisposableHolder ->
        ViewModelImpl(
                rxLifecycle = rxlc(fragment, compositeDisposableHolder),
                loaderHolder = loaderHolder(),
                loadingProgressHelper = loadingProgressHelper(),
                args = fragment.arguments ?: argsOf(),
                toaster = toaster(),
                actionHandler = contextActionHandler(fragment.activity, compositeDisposableHolder)
        )
    }

    //endregion
}