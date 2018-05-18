package com.stepango.archetype.player.ui.episodes

import android.databinding.ObservableField
import android.os.Bundle
import com.stepango.archetype.R
import com.stepango.archetype.activity.BaseActivity
import com.stepango.archetype.databinding.ScreenEpisodesBinding
import com.stepango.archetype.fragment.BaseFragment
import com.stepango.archetype.player.data.wrappers.EpisodeListItemWrapper
import com.stepango.archetype.player.data.wrappers.EpisodeListItemWrapperFabric
import com.stepango.archetype.player.di.injector
import com.stepango.archetype.player.di.lazyInject
import com.stepango.archetype.viewmodel.ViewModel
import com.stepango.rxdatabindings.setTo

class EpisodesActivity : BaseActivity() {
    override val fragmentProducer = { EpisodesFragment() }
}

class EpisodesFragment : BaseFragment<ScreenEpisodesBinding>() {

    override fun initBinding(binding: ScreenEpisodesBinding, state: Bundle?) {
        val vm = injector.injector.vm(this)
        binding.vm = EpisodesViewModel(injector.episodeListItemWrapperFabric(vm.actionHandler), vm)
    }

    override val layoutId = R.layout.screen_episodes
}

class EpisodesViewModel(
        val wrapper: EpisodeListItemWrapperFabric,
        vm: ViewModel
) :
        ViewModel by vm {

    private val episodesUseCase by lazyInject { episodesUseCase() }
    val episodes: ObservableField<List<EpisodeListItemWrapper>> = ObservableField(listOf())

    init {
        refresh()
        display()
    }

    private fun display() {
        episodesUseCase.observeEpisodes()
                .map { wrapper.wrap(it) }
                .setTo(episodes)
                .bindSubscribe()
    }

    fun refresh() {
        episodesUseCase.updateEpisodes(actionHandler)
                .bindSubscribe(
                        onComplete = { hideLoader() },
                        onError = { toaster.showError(it, R.string.episodes_error_loading) }
                )
    }
}