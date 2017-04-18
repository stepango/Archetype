package com.stepango.archetype.player.ui.player

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri


interface PlayerComponent {
    fun play(url: String)
}

class PlayerComponentImpl(val context: Context) : PlayerComponent {

    override fun play(url: String) {
        val mediaPlayer = MediaPlayer.create(context, Uri.parse(url))
        mediaPlayer.start()
    }

}
