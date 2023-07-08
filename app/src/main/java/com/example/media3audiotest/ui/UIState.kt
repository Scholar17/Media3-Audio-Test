package com.example.media3audiotest.ui

sealed class UIState{
    object Initial: UIState()
    object Buffering: UIState()
    object Ready: UIState()
}
