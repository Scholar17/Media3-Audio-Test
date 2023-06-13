package com.example.media3audiotest

import android.media.browse.MediaBrowser.MediaItem
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.media3.common.MediaMetadata
import com.example.media3audiotest.ui.PlayerEvent
import com.example.media3audiotest.ui.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
    private val mediaServiceHandler: MediaServiceHandler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    @OptIn(SavedStateHandleSaveableApi::class)
    var duration by savedStateHandle.saveable { mutableStateOf(0L) }
    var progress by savedStateHandle.saveable { mutableStateOf(0f) }
    var progressString by savedStateHandle.saveable { mutableStateOf("00:00") }
    var isPlaying by savedStateHandle.saveable { mutableStateOf(false) }

    private val _uiState = MutableStateFlow<UIState>(UIState.Initial)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            loadData()

            mediaServiceHandler.mediaState.collect { mediaState ->
                when (mediaState) {
                    is MediaState.Buffering -> {
                        calculateProgressValue(mediaState.progress)
                    }

                    MediaState.Initial -> {
                        _uiState.value = UIState.Initial
                    }

                    is MediaState.Playing -> {
                        isPlaying = mediaState.isPlaying
                    }

                    is MediaState.Progress -> {
                        calculateProgressValue(mediaState.progress)
                    }

                    is MediaState.Ready -> {
                        duration = mediaState.duration
                        _uiState.value = UIState.Ready
                    }
                }
            }
        }
    }

    fun onUiEvent(playerEvent: PlayerEvent) = viewModelScope.launch {
        when (playerEvent) {
            PlayerEvent.Backward -> {
                mediaServiceHandler.onPlayerEvent(PlayerEvent.Backward)
            }

            PlayerEvent.Forward -> {
                mediaServiceHandler.onPlayerEvent(PlayerEvent.Forward)
            }

            PlayerEvent.PlayPause -> {
                mediaServiceHandler.onPlayerEvent(PlayerEvent.PlayPause)
            }

            PlayerEvent.Stop -> {
                mediaServiceHandler.onPlayerEvent(PlayerEvent.Stop)
            }

            is PlayerEvent.UpdateProgress -> {
                progress = playerEvent.newProgress
                mediaServiceHandler.onPlayerEvent(PlayerEvent.UpdateProgress(progress))
            }
        }
    }

    fun formatDuration(duration: Long): String {
        val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds = (TimeUnit.SECONDS.convert(
            duration,
            TimeUnit.MILLISECONDS
        ) - minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun calculateProgressValue(currentProgress: Long) {
        progress = if (currentProgress > 0) currentProgress.toFloat() / duration else 0f
        progressString = formatDuration(currentProgress)
    }


    private fun loadData() {
        val mediaItem = androidx.media3.common.MediaItem.Builder()
            .setUri("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3")
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setFolderType(MediaMetadata.FOLDER_TYPE_ALBUMS)
                    .setArtworkUri(Uri.parse("https://i.pinimg.com/736x/4b/02/1f/4b021f002b90ab163ef41aaaaa17c7a4.jpg"))
                    .setDisplayTitle("Audio Player")
                    .build()
            ).build()
        mediaServiceHandler.addMediaItem(mediaItem)
    }
}

