package com.stepango.archetype.player.ui.player

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.stepango.archetype.logger.logger


interface PlayerComponent {
    fun play(url: String)
}

class PlayerComponentImpl(val context: Context) : PlayerComponent {

    override fun play(url: String) {
        logger.d("play from $url")
        val mediaPlayer = MediaPlayer.create(context, Uri.parse(url))
        mediaPlayer.start()
    }

}
