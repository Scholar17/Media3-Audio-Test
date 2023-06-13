package com.example.media3audiotest

import android.annotation.SuppressLint
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.media3audiotest.ui.PlayerEvent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MediaServiceHandler @Inject constructor(
    private val exoPlayer: ExoPlayer
) : Player.Listener {

    private val _mediaState = MutableStateFlow<MediaState>(MediaState.Initial)
    val mediaState = _mediaState.asStateFlow()

    private var job: Job? = null

    init {
        exoPlayer.addListener(this)
        job = Job()
    }

    fun addMediaItem(mediaItem: MediaItem) {
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }

    suspend fun onPlayerEvent(playerEvent: PlayerEvent) {
        when (playerEvent) {
            PlayerEvent.Backward -> {
                exoPlayer.seekBack()
            }

            PlayerEvent.Forward -> {
                exoPlayer.seekForward()
            }

            PlayerEvent.PlayPause -> {
                if (exoPlayer.isPlaying) {
                    exoPlayer.pause()
                    stopProgressUpdate()
                } else {
                    exoPlayer.play()
                    _mediaState.value = MediaState.Playing(isPlaying = true)
                    startProgressUpdate()
                }
            }

            PlayerEvent.Stop -> {
                stopProgressUpdate()
            }

            is PlayerEvent.UpdateProgress -> {
                 exoPlayer.seekTo((exoPlayer.duration * playerEvent.newProgress).toLong())
            }
        }
    }

    @SuppressLint("SwitchIntDef")
    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_BUFFERING -> _mediaState.value =
                MediaState.Buffering(exoPlayer.currentPosition)
            ExoPlayer.STATE_READY -> _mediaState.value =
                MediaState.Ready(exoPlayer.duration)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        _mediaState.value = MediaState.Playing(isPlaying = isPlaying)
        if (isPlaying) {
            GlobalScope.launch(Dispatchers.Main) {
                startProgressUpdate()
            }
        } else {
            stopProgressUpdate()
        }
    }

    private suspend fun startProgressUpdate() = job.run {
        while (true) {
            delay(500L)
            _mediaState.value = MediaState.Progress(exoPlayer.currentPosition)
        }
    }

    private fun stopProgressUpdate() {
        job?.cancel()
        _mediaState.value = MediaState.Playing(false)
    }


}