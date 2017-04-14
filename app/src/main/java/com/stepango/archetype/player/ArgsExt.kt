package com.stepango.archetype.player

import com.stepango.archetype.action.Args


const val EPISODE_ID = "$PREFIX.episode_id"

fun Args.episodeId() = this.getLong(EPISODE_ID)
inline fun Args.episodeId(block: () -> Long) = apply { this.putLong(EPISODE_ID, block()) }