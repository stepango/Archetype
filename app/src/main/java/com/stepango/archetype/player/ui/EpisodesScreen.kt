package com.stepango.archetype.player.ui

import android.databinding.BindingAdapter
import android.databinding.ObservableField
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.StableId
import com.ninetyseconds.auckland.core.recycler.swap
import com.ninetyseconds.auckland.core.recycler.with
import com.ninetyseconds.auckland.core.viewmodel.ViewModel
import com.ninetyseconds.auckland.core.viewmodel.ViewModelImpl
import com.ninetyseconds.auckland.lastadapter.episodeItemType
import com.stepango.archetype.R
import com.stepango.archetype.activity.BaseActivity
import com.stepango.archetype.databinding.ScreenEpisodesBinding
import com.stepango.archetype.fragment.BaseFragment
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

    val episodes = ObservableField<List<EpisodesWrapper>>(listOf())

    init {
        refreshItems()
    }

    private fun refreshItems() {
        episodes.set(listOf(
                EpisodesWrapper("aaa", 0),
                EpisodesWrapper("aab", 1),
                EpisodesWrapper("abb", 2),
                EpisodesWrapper("bbb", 3)
        ))
    }
}

@BindingAdapter("episodesAdapter")
fun episodesAdapter(view: RecyclerView, list: List<EpisodesWrapper>) {
    LastAdapter.with(list)
            .type { episodeItemType }
            .swap(view)
}

data class EpisodesWrapper(val name: String, override val stableId: Long) : StableId

