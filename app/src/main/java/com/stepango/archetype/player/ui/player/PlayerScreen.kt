package com.stepango.archetype.player.ui.player

import android.content.Context
import android.databinding.ObservableField
import android.os.Bundle
import com.stepango.archetype.R
import com.stepango.archetype.action.IntentMaker
import com.stepango.archetype.action.argsOf
import com.stepango.archetype.action.intent
import com.stepango.archetype.activity.BaseActivity
import com.stepango.archetype.databinding.ScreenPlayerBinding
import com.stepango.archetype.fragment.BaseFragment
import com.stepango.archetype.player.data.wrappers.EpisodeItemWrapperFabric
import com.stepango.archetype.player.data.wrappers.EpisodeWrapper
import com.stepango.archetype.player.di.injector
import com.stepango.archetype.player.di.lazyInject
import com.stepango.archetype.player.episodeId
import com.stepango.archetype.viewmodel.ViewModel
import com.stepango.rxdatabindings.setTo

class PlayerActivity : BaseActivity() {
    override val fragmentProducer = { PlayerFragment() }

    companion object {
        fun intent(episodeId: Long, intentMaker: IntentMaker, context: Context) = intentMaker.intent<PlayerActivity>(context, argsOf { episodeId { episodeId } })
    }
}

class PlayerFragment : BaseFragment<ScreenPlayerBinding>() {

    override fun initBinding(binding: ScreenPlayerBinding, state: Bundle?) {
        val vm = injector.vm(this)
        binding.vm = PlayerViewModel(vm, injector.episodeItemWrapperFabric(vm.actionHandler))
    }

    override val layoutId = R.layout.screen_player
}

class PlayerViewModel(
        vm: ViewModel,
        val wrapper: EpisodeItemWrapperFabric
) : ViewModel by vm {

    private val episodesUseCase by lazyInject { episodesUseCase() }
    val player by lazyInject { player() }

    val episodeId = args().episodeId()
    val episode = ObservableField<EpisodeWrapper>()

    init {
        episodesUseCase.observeEpisode(episodeId)
                .map { wrapper.wrap(it) }
                .setTo(episode)
                .bindSubscribe()
    }

    fun play() {
        if (episode.get()?.file.isNullOrEmpty())
            player.play(episode.get()?.audioUrl!!)
        else
            player.play(episode.get()?.file!!)
    }

}
