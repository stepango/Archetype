package com.stepango.archetype.player.di

import android.util.SparseArray
import com.stepango.archetype.R
import com.stepango.archetype.action.ActionHandler
import com.stepango.archetype.action.ContextAction
import com.stepango.archetype.action.MainActionHandler
import com.stepango.archetype.glide.GlideImageLoader
import com.stepango.archetype.image.ImageLoader
import com.stepango.archetype.player.actions.IdleAction
import com.stepango.archetype.player.db.EpisodesModelRepo
import com.stepango.archetype.player.db.memory.InMemoryEpisodesRepo
import com.stepango.archetype.player.ui.additional.MockToaster
import com.stepango.archetype.ui.Toaster

val Any.injector: Injector by lazy { InjectorImpl() }

inline fun <T> lazyInject(crossinline block: Injector.() -> T): Lazy<T> = lazy { Injector().block() }

interface Injector {
    fun mainActionHandler(): ActionHandler
    fun imageLoader(): ImageLoader
    fun toaster(): Toaster

    //region repositories
    fun episodesRepo(): EpisodesModelRepo
    //endregion

    companion object {
        operator fun invoke(): Injector = injector
    }
}

class InjectorImpl : Injector {
    override fun mainActionHandler(): ActionHandler = MainActionHandler(actions)

    override fun imageLoader() = GlideImageLoader()

    //TODO: replace by SimpleToaster with context
    override fun toaster(): Toaster = MockToaster()

    //region repositories
    override fun episodesRepo() = InMemoryEpisodesRepo()
    //endregion
}

val actions = SparseArray<ContextAction>().apply {
    put(R.id.action_idle, IdleAction())
}
