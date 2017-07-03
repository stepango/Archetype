package com.stepango.archetype.player.data.db.model

import com.stepango.archetype.R
import io.mironov.smuggler.AutoParcelable

data class EpisodesModel(
        val name: String,
        val summary: String,
        val iconUrl: String,
        val audioUrl: String,
        val content: String?,
        var state: EpisodeDownloadState = EpisodeDownloadState.DOWNLOAD,
        var file: String? = null,
        val id: Long = name.hashCode().toLong()
) : AutoParcelable

enum class EpisodeDownloadState {
    DOWNLOAD {
        override val action = R.id.action_download_episode
        override val textId = R.string.action_download_episode
    },

    WAIT {
        override val action = R.id.action_idle
        override val textId = R.string.action_wait_for_download
    },

    CANCEL {
        override val action = R.id.action_cancel_download_episode
        override val textId = R.string.action_cancel_download_episode
    },

    RETRY {
        override val action = R.id.action_download_episode
        override val textId = R.string.action_retry_download_episode
    };

    abstract val action: Int
    abstract val textId: Int
    fun isWait(): Boolean = this == WAIT
}

