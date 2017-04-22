package com.stepango.archetype.player.ui.episodes

import android.databinding.BindingAdapter
import android.databinding.ObservableField
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.stepango.archetype.R
import com.stepango.archetype.activity.BaseActivity
import com.stepango.archetype.databinding.ItemEpisodeBinding
import com.stepango.archetype.databinding.ScreenEpisodesBinding
import com.stepango.archetype.fragment.BaseFragment
import com.stepango.archetype.player.data.wrappers.EpisodeListItemWrapper
import com.stepango.archetype.player.di.lazyInject
import com.stepango.archetype.ui.swap
import com.stepango.archetype.ui.with
import com.stepango.archetype.viewmodel.ViewModel
import com.stepango.archetype.viewmodel.ViewModelImpl
import com.stepango.rxdatabindings.setTo
import com.trello.navi2.NaviComponent

class EpisodesActivity : BaseActivity() {
    override val fragmentProducer = { EpisodesFragment() }
}

class EpisodesFragment : BaseFragment<ScreenEpisodesBinding>() {

    override fun initBinding(binding: ScreenEpisodesBinding, state: Bundle?) {
        binding.vm = EpisodesViewModel(this)
    }

    override val layoutId = R.layout.screen_episodes
}

class EpisodesViewModel(
        naviComponent: NaviComponent
) :
        ViewModel by ViewModelImpl(naviComponent = naviComponent) {

    val episodesComponent by lazyInject { episodesComponent() }
    val episodes: ObservableField<List<EpisodeListItemWrapper>> = ObservableField(listOf())

    init {
        refresh()
        display()
    }

    private fun display() {
        episodesComponent.observeEpisodes()
                .setTo(episodes)
                .bindSubscribe()
    }

    fun refresh() {
        episodesComponent.updateEpisodes()
                .bindSubscribe(
                        onComplete = { hideLoader() },
                        onError = { toaster.showError(it, R.string.episodes_error_loading) }
                )
    }
}

@BindingAdapter("episodesAdapter")
fun episodesAdapter(view: RecyclerView, list: List<EpisodeListItemWrapper>) {
    LastAdapter.with(list)
            .type { Type<ItemEpisodeBinding>(R.layout.item_episode) }
            .swap(view)
}
