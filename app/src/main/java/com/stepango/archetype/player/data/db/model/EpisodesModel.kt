package com.stepango.archetype.player.data.db.model

import io.mironov.smuggler.AutoParcelable

data class EpisodesModel(
        val name: String,
        val summary: String,
        val iconUrl: String,
        val audioUrl: String,
        val content: String?,
        val id: Long = name.hashCode().toLong()
) : AutoParcelable

