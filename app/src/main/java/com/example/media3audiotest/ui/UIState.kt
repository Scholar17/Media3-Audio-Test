package com.example.media3audiotest.ui

sealed class UIState{
    object Initial: UIState()
    object Ready: UIState()
}
