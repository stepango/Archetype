package com.stepango.archetype.player.ui

import android.databinding.BindingAdapter
import android.databinding.ObservableField
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.StableId
import com.stepango.archetype.R
import com.stepango.archetype.activity.BaseActivity
import com.stepango.archetype.databinding.ScreenEpisodesBinding
import com.stepango.archetype.fragment.BaseFragment
import com.stepango.archetype.lastadapter.episodeItemType
import com.stepango.archetype.player.db.model.EpisodesModel
import com.stepango.archetype.player.di.lazyInject
import com.stepango.archetype.rx.filterNotEmpty
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
) : ViewModel by ViewModelImpl(naviComponent) {

    val episodesRepo by lazyInject { episodesRepo() }

    val episodes = ObservableField<List<EpisodesWrapper>>(listOf())

    init {
        episodesRepo.observeAll()
                .filterNotEmpty()
                .map { it.map(::EpisodesWrapper) }
                .setTo(episodes) { it }
                //TODO where we should pass subscribeOn io?
                .bindSubscribe()
        refreshItems()
    }

    private fun refreshItems() {
        episodesRepo.pull()
                //TODO where we should pass subscribeOn io?
                .bindSubscribe(
                        onError = { toaster.showError(it, R.string.episodes_error_loading) }
                )
    }
}

@BindingAdapter("episodesAdapter")
fun episodesAdapter(view: RecyclerView, list: List<EpisodesWrapper>) {
    LastAdapter.with(list)
            .type { episodeItemType }
            .swap(view)
}

class EpisodesWrapper(model: EpisodesModel) : StableId {
    override val stableId: Long = model.hashCode().toLong()
    val name: String = model.name
}

