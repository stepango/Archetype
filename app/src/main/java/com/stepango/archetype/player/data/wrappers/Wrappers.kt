package com.stepango.archetype.player.data.wrappers

import com.github.nitrico.lastadapter.StableId
import com.stepango.archetype.action.Args
import com.stepango.archetype.action.argsOf
import com.stepango.archetype.player.data.db.model.EpisodesModel
import com.stepango.archetype.player.episodeId

class EpisodesWrapper(model: EpisodesModel) : StableId, ArgsHolder {
    override val stableId: Long = model.id
    val name: String = model.name
    val description: String = model.description
    override fun args() = argsOf { episodeId { stableId } }
}


interface ArgsHolder {
    fun args(): Args = argsOf()
}