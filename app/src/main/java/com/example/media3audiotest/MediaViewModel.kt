package com.example.media3audiotest

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.example.media3audiotest.ui.PlayerEvent
import com.example.media3audiotest.ui.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import androidx.media3.common.MediaItem

@HiltViewModel
class MediaViewModel @Inject constructor(
    private val mediaServiceHandler: MediaServiceHandler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    @OptIn(SavedStateHandleSaveableApi::class)
    var runText = mutableListOf<Long>(3220L, 372715L)
    var duration by savedStateHandle.saveable { mutableStateOf(0L) }
    var progress by savedStateHandle.saveable { mutableStateOf(0f) }
    var currentMediaIndex by savedStateHandle.saveable { mutableStateOf(0) }
    var onGoingProgressString by savedStateHandle.saveable { mutableStateOf("00:00") }
    var progressString by savedStateHandle.saveable { mutableStateOf("00:00") }
    var isPlaying by savedStateHandle.saveable { mutableStateOf(false) }

    private val _uiState = MutableStateFlow<UIState>(UIState.Initial)
    val uiState = _uiState.asStateFlow()
    private var mediaItem = androidx.media3.common.MediaItem.Builder()


    init {
        viewModelScope.launch {
            addMediaItems()
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
                        Log.d("duration", "$duration")
                        onGoingProgressString = formatDuration(duration = duration)
                        _uiState.value = UIState.Ready
                    }
                }
            }
        }

    }

    private fun addMediaItems() {
        val urls = mutableListOf<String>()
        urls.add(0, "https://galaxyshopbucket.s3.ap-southeast-1.amazonaws.com/CHAT_MEDIA_FILES/10022da67fd6-daa6-4ff9-bc42-f99228aaef38.mp3")
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
                currentMediaIndex = playerEvent.audioIndex
                Log.d("meeediaIndex", "$currentMediaIndex")
                mediaServiceHandler.onPlayerEvent(PlayerEvent.PlayPause(audioIndex = playerEvent.audioIndex))
            }

            is PlayerEvent.UpdateProgress -> {
                progress = playerEvent.newProgress
                mediaServiceHandler.onPlayerEvent(PlayerEvent.UpdateProgress(progress))
            }

        }
    }

    fun formatOnGoingDuration(duration: Long, currentProgress: Long): String {
        val calculateValue = runText[currentMediaIndex] - currentProgress
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
        val mediaItems : MutableList<MediaItem> = mutableListOf()
        for (element in urls) {
            mediaItems.add(MediaItem.Builder().setUri(element).build())
        }
        mediaServiceHandler.addMediaItems(mediaItem = mediaItems)
    }
}

