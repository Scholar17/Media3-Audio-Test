package com.example.media3audiotest

import android.annotation.SuppressLint
import android.util.Log
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
    private val exoPlayer: ExoPlayer,

) : Player.Listener {

    private val _mediaState = MutableStateFlow<MediaState>(MediaState.Initial)
    val mediaState = _mediaState.asStateFlow()
    var mediaItemList = mutableListOf<MediaItem>()


    private var job: Job? = null

    init {
        exoPlayer.addListener(this)
        job = Job()
    }

    fun addMediaItem(mediaItem: MediaItem) {
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }

    fun addMediaItems(mediaItem: List<MediaItem>) {
        mediaItemList.addAll(mediaItem)
        exoPlayer.setMediaItems(mediaItem)
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

            is PlayerEvent.PlayPause -> {
                if (exoPlayer.isPlaying) {
                    if (exoPlayer.currentMediaItemIndex == playerEvent.audioIndex) {
                        exoPlayer.pause()
                        stopProgressUpdate()
                    } else {
                        exoPlayer.pause()
                        stopProgressUpdate()
                        exoPlayer.seekTo(playerEvent.audioIndex, 0L)
                        exoPlayer.play()
                        startProgressUpdate()
                    }
                } else {
                    if (exoPlayer.currentMediaItemIndex != playerEvent.audioIndex) {
                        exoPlayer.seekToDefaultPosition()
                        exoPlayer.seekTo(playerEvent.audioIndex, 0L)
                    }
                    exoPlayer.playWhenReady
                    exoPlayer.play()
                    startProgressUpdate()
                    _mediaState.value = MediaState.Playing(isPlaying = true)
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

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        Log.d("addddingmediaitem", "${mediaItem?.mediaId}")

//        _mediaState.value = MediaState.Loading(true)
    }

    @SuppressLint("SwitchIntDef")
    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_BUFFERING -> _mediaState.value =
                MediaState.Buffering(exoPlayer.currentPosition)
            ExoPlayer.STATE_READY -> _mediaState.value =
                MediaState.Ready(exoPlayer.duration)
            ExoPlayer.STATE_ENDED -> {
                _mediaState.value = MediaState.Playing(false)
                exoPlayer.seekTo(0L)
                exoPlayer.pause()
            }
        }
    }

    override fun onIsLoadingChanged(isLoading: Boolean) {
        super.onIsLoadingChanged(isLoading)
        if (isLoading) {
            _mediaState.value = MediaState.Loading(true)
        } else {
            _mediaState.value = MediaState.Loading(false)
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



    override fun onPositionDiscontinuity(
        oldPosition: Player.PositionInfo,
        newPosition: Player.PositionInfo,
        reason: Int
    ) {
        super.onPositionDiscontinuity(oldPosition, newPosition, reason)
        if (reason == Player.DISCONTINUITY_REASON_AUTO_TRANSITION) {
            _mediaState.value = MediaState.Playing(false)
            exoPlayer.seekTo(oldPosition.mediaItemIndex, 0L)
            exoPlayer.pause()
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