package com.stepango.archetype.player.data.db.model

data class EpisodesModel(
        val name: String,
        val description: String,
        val id: Long = name.hashCode().toLong()
)

