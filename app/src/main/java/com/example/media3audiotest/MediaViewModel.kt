package com.example.media3audiotest

import android.provider.MediaStore.Audio.Media
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.media3.common.MediaItem
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
    var durationList = mutableListOf<String>("00:03", "06:12")
    var duration by savedStateHandle.saveable { mutableStateOf(0L) }
    var progress by savedStateHandle.saveable { mutableStateOf(0f) }
    var currentMediaIndex by savedStateHandle.saveable { mutableStateOf(0) }
    var onGoingProgressString by savedStateHandle.saveable { mutableStateOf("00:00") }
    var progressString by savedStateHandle.saveable { mutableStateOf("00:00") }
    var isPlaying by savedStateHandle.saveable { mutableStateOf(false) }
    var isReady by savedStateHandle.saveable { mutableStateOf(false) }
    var isLoading by savedStateHandle.saveable { mutableStateOf(false) }
    var isStartPlay by savedStateHandle.saveable { mutableStateOf(false) }

    private val _uiState = MutableStateFlow<UIState>(UIState.Initial)
    val uiState = _uiState.asStateFlow()
    private var mediaItem = MediaItem.Builder()


    init {
        viewModelScope.launch {
            addMediaItems()
            mediaServiceHandler.mediaState.collect { mediaState ->
                when (mediaState) {
                    is MediaState.Buffering -> {
                        isReady = false
                        _uiState.value = UIState.Buffering
                        calculateProgressValue(mediaState.progress)
                    }

                    MediaState.Initial -> {
                        isReady = false
                        _uiState.value = UIState.Initial
                    }

                    is MediaState.Playing -> {
                        isPlaying = mediaState.isPlaying
                        isReady = true
                    }

                    is MediaState.Progress -> {
                            calculateProgressValue(mediaState.progress)
                    }

                    is MediaState.Ready -> {
                        isReady = true
                        duration = mediaState.duration
                        Log.d("duration", "$duration")
                        onGoingProgressString = formatDuration(duration = duration)
                        _uiState.value = UIState.Ready
                    }

                    is MediaState.Loading -> {
                        isReady = !mediaState.isLoading
                        isLoading = mediaState.isLoading
                    }

                    is MediaState.StartPlay -> {
                        isStartPlay = mediaState.isStartPlay
                        Log.d("isStartPlay", "$isStartPlay")
                    }
                }
            }
        }
    }

    private fun addMediaItems() {
        val urls = mutableListOf<String>()
        urls.add(
            0,
            "https://galaxyshopbucket.s3.ap-southeast-1.amazonaws.com/CHAT_MEDIA_FILES/10022da67fd6-daa6-4ff9-bc42-f99228aaef38.mp3"
        )
        urls.add(1, "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3")
        loadData(urls)
    }

    fun onUiEvent(playerEvent: PlayerEvent) = viewModelScope.launch {
        when (playerEvent) {
            PlayerEvent.Backward -> {
                mediaServiceHandler.onPlayerEvent(PlayerEvent.Backward)
            }

            PlayerEvent.Forward -> {
                mediaServiceHandler.onPlayerEvent(PlayerEvent.Forward)
            }

            PlayerEvent.Stop -> {
                mediaServiceHandler.onPlayerEvent(PlayerEvent.Stop)
            }

            is PlayerEvent.PlayPause -> {
                Log.d("meeediaIndex", "$currentMediaIndex")
                isReady = false
                currentMediaIndex = playerEvent.audioIndex
                mediaServiceHandler.onPlayerEvent(PlayerEvent.PlayPause(audioIndex = playerEvent.audioIndex))
            }

            is PlayerEvent.UpdateProgress -> {
                progress = playerEvent.newProgress
                mediaServiceHandler.onPlayerEvent(PlayerEvent.UpdateProgress(progress))
            }

        }
    }

    private fun formatOnGoingDuration(duration: Long, currentProgress: Long): String {
        val calculateValue = duration - currentProgress
        val minutes = TimeUnit.MINUTES.convert(calculateValue, TimeUnit.MILLISECONDS)
        val seconds = (TimeUnit.SECONDS.convert(
            calculateValue,
            TimeUnit.MILLISECONDS
        ) - minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
        return String.format("%02d:%02d", minutes, seconds)
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
        onGoingProgressString =
            formatOnGoingDuration(duration = duration, currentProgress = currentProgress)
    }


    private fun loadData(urls: List<String>) {
        val mediaItems: MutableList<MediaItem> = mutableListOf()
        for (element in urls) {
            mediaItems.add(MediaItem.Builder().setUri(element).build())
        }
        mediaServiceHandler.addMediaItems(mediaItem = mediaItems)
    }

    private fun loadData(url: String) {
        mediaItem.setUri(url).build()
        mediaServiceHandler.addMediaItem(mediaItem = mediaItem.build())
    }
}

