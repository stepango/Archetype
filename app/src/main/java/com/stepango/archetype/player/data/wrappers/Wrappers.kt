package com.stepango.archetype.player.data.wrappers

import android.view.View
import com.github.nitrico.lastadapter.StableId
import com.stepango.archetype.action.Args
import com.stepango.archetype.action.argsOf
import com.stepango.archetype.player.data.db.model.EpisodeDownloadState
import com.stepango.archetype.player.data.db.model.EpisodesModel
import com.stepango.archetype.player.episodeId
import com.stepango.archetype.util.firstLine
import com.stepango.archetype.util.linesCount
import io.mironov.smuggler.AutoParcelable

data class EpisodeListItemWrapper(private val model: EpisodesModel) : StableId, ArgsHolder, AutoParcelable {
    override val stableId: Long = model.id
    val name: String = model.name
    val imageUrl: String = model.iconUrl
    override fun args(): Args = argsOf { episodeId { stableId } }
}

data class EpisodeWrapper(private val model: EpisodesModel) : AutoParcelable {
    val name: String = model.name
    val summary: String = model.summary.run { if (this.linesCount() > 1) this.firstLine() else this }
    val content: String = model.content ?: model.summary.run { if (this.linesCount() > 1) this else "" }
    val audioUrl: String = model.audioUrl
    val state: EpisodeDownloadState = model.state
    fun downloadButtonVisibility() = if (model.file == null) View.VISIBLE else View.GONE
}

interface ArgsHolder {
    fun args(): Args = argsOf()
}