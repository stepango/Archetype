package com.stepango.archetype.player.ui.episodes

import android.databinding.ObservableField
import android.os.Bundle
import com.stepango.archetype.R
import com.stepango.archetype.action.Args
import com.stepango.archetype.activity.BaseActivity
import com.stepango.archetype.databinding.ScreenPlayerBinding
import com.stepango.archetype.fragment.BaseFragment
import com.stepango.archetype.player.data.wrappers.EpisodesWrapper
import com.stepango.archetype.player.di.lazyInject
import com.stepango.archetype.player.episodeId
import com.stepango.archetype.rx.filterNonEmpty
import com.stepango.archetype.viewmodel.ViewModel
import com.stepango.archetype.viewmodel.ViewModelImpl
import com.stepango.rxdatabindings.setTo
import com.trello.navi2.NaviComponent

class PlayerActivity : BaseActivity() {
    override val fragmentProducer = { PlayerFragment() }
}

class PlayerFragment : BaseFragment<ScreenPlayerBinding>() {

    override fun initBinding(binding: ScreenPlayerBinding, state: Bundle?) {
        binding.vm = PlayerViewModel(this, arguments)
    }

    override val layoutId = R.layout.screen_player
}

class PlayerViewModel(
        naviComponent: NaviComponent,
        arguments: Args
) : ViewModel by ViewModelImpl(naviComponent = naviComponent, args = arguments) {

    val episodesRepo by lazyInject { episodesRepo() }

    val player by lazyInject { player() }

    val episode = ObservableField<EpisodesWrapper>()

    init {
        episodesRepo.observe(args().episodeId())
                .filterNonEmpty()
                .map(::EpisodesWrapper)
                .setTo(episode)
                .bindSubscribe()
    }

}
