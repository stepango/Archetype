package com.stepango.archetype.player.data.wrappers

import com.github.nitrico.lastadapter.Type
import com.stepango.archetype.R
import com.stepango.archetype.action.ActionData
import com.stepango.archetype.action.ContextActionHandler
import com.stepango.archetype.action.execute
import com.stepango.archetype.action.noParams
import com.stepango.archetype.action.with
import com.stepango.archetype.databinding.ItemEpisodeBinding
import com.stepango.archetype.player.data.db.model.EpisodeDownloadState
import com.stepango.archetype.player.data.db.model.EpisodesModel
import com.stepango.archetype.player.loader.CancelDownloadEpisodeAction
import com.stepango.archetype.player.loader.DownloadEpisodeAction
import com.stepango.archetype.player.loader.DownloadEpisodeActionParams
import com.stepango.archetype.player.ui.player.ShowEpisodeAction
import com.stepango.archetype.player.ui.player.ShowEpisodeActionParams
import com.stepango.archetype.ui.LastAdapterItem
import com.stepango.archetype.util.firstLine
import com.stepango.archetype.util.linesCount

interface EpisodeListItemWrapperFabric {
    fun wrap(models: List<EpisodesModel>): List<EpisodeListItemWrapper>
}

class EpisodeListItemWrapperFabricImpl(
        private val ah: ContextActionHandler,
        private val showEpisodeAction: ShowEpisodeAction,
        private val cancelDownloadEpisodeAction: CancelDownloadEpisodeAction,
        private val downloadEpisodeAction: DownloadEpisodeAction
) : EpisodeListItemWrapperFabric {
    override fun wrap(models: List<EpisodesModel>): List<EpisodeListItemWrapper> = models.map {
        EpisodeListItemWrapper(it, ah, showEpisodeAction, cancelDownloadEpisodeAction, downloadEpisodeAction)
    }
}

class EpisodeListItemWrapper(
        model: EpisodesModel,
        private val ah: ContextActionHandler,
        private val showEpisodeAction: ShowEpisodeAction,
        private val cancelDownloadEpisodeAction: CancelDownloadEpisodeAction,
        private val downloadEpisodeAction: DownloadEpisodeAction
) : LastAdapterItem {
    override val stableId: Long = model.id
    val name: String = model.name
    val imageUrl: String = model.iconUrl
    val state: EpisodeDownloadState = model.state
    val isDownloaded: Boolean = model.file != null

    override fun getBindingType() = Type<ItemEpisodeBinding>(R.layout.item_episode)

    fun open() = showEpisodeAction.with(ShowEpisodeActionParams(episodeId = stableId)).execute(ah)

    fun action() {
        when (state) {
            EpisodeDownloadState.WAIT   -> ActionData.IDLE
            EpisodeDownloadState.CANCEL -> cancelDownloadEpisodeAction.noParams()
            EpisodeDownloadState.DOWNLOAD,
            EpisodeDownloadState.RETRY  -> downloadEpisodeAction.with(DownloadEpisodeActionParams(stableId))
        }.execute(ah)
    }
}


interface EpisodeItemWrapperFabric {
    fun wrap(model: EpisodesModel): EpisodeWrapper
}

class EpisodeItemWrapperFabricImpl(
        private val ah: ContextActionHandler,
        private val cancelDownloadEpisodeAction: CancelDownloadEpisodeAction,
        private val downloadEpisodeAction: DownloadEpisodeAction
) : EpisodeItemWrapperFabric {
    override fun wrap(model: EpisodesModel): EpisodeWrapper = EpisodeWrapper(
            model,
            cancelDownloadEpisodeAction,
            downloadEpisodeAction,
            ah)
}

class EpisodeWrapper(
        model: EpisodesModel,
        private val cancelDownloadEpisodeAction: CancelDownloadEpisodeAction,
        private val downloadEpisodeAction: DownloadEpisodeAction,
        private val ah: ContextActionHandler
) {
    val episodeId: Long = model.id
    val name: String = model.name
    val summary: String = model.summary.run { if (this.linesCount() > 1) this.firstLine() else this }
    val content: String = model.content ?: model.summary.run { if (this.linesCount() > 1) this else "" }
    val audioUrl: String = model.audioUrl
    val state: EpisodeDownloadState = model.state
    val file: String? = model.file
    val isDownloaded: Boolean = model.file != null

    fun action() {
        when (state) {
            EpisodeDownloadState.WAIT   -> ActionData.IDLE
            EpisodeDownloadState.CANCEL -> cancelDownloadEpisodeAction.noParams()
            EpisodeDownloadState.DOWNLOAD,
            EpisodeDownloadState.RETRY  -> downloadEpisodeAction.with(DownloadEpisodeActionParams(episodeId))
        }.execute(ah)
    }
}