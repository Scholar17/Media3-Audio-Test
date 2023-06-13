package com.example.media3audiotest.ui

sealed class PlayerEvent {
    object PlayPause: PlayerEvent()
    object Forward: PlayerEvent()
    object Backward: PlayerEvent()
    object Stop: PlayerEvent()
    data class UpdateProgress(val newProgress: Float) : PlayerEvent()
}

