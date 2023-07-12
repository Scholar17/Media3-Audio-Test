package com.example.media3audiotest.ui

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.media3audiotest.MediaViewModel
import com.example.media3audiotest.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleAudioPlayerScreen(
    vm: MediaViewModel,
    startService: () -> Unit,
) {
    val state = vm.uiState.collectAsState()
    var pauseByLifeCycle by rememberSaveable { mutableStateOf(false) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(key1 = lifecycle) {
        val observer = LifecycleEventObserver { _, event ->

            when(event) {
                Lifecycle.Event.ON_CREATE -> {}
                Lifecycle.Event.ON_START -> {}
                Lifecycle.Event.ON_RESUME -> {
                    if (pauseByLifeCycle) {
                        pauseByLifeCycle = false
                        Log.d("onResume", "$pauseByLifeCycle")
                        vm.playPlayer()
                    }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    if (vm.isPlaying) {
                        vm.pausePlayer()
                        pauseByLifeCycle = true
                        Log.d("onPause", "$pauseByLifeCycle")
                    }

                }
                Lifecycle.Event.ON_STOP -> {

                }
                Lifecycle.Event.ON_DESTROY -> {}
                Lifecycle.Event.ON_ANY -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        modifier = Modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.title),
                        style = MaterialTheme.typography.titleSmall
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(Color.Gray)
            )
        },
        content = { paddingValues ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)) {
                Column(modifier = Modifier) {
                    ReceiverAudioItem(
                        senderName = "TZO",
                        onUIEvent = vm::onUiEvent,
                        durationString = vm.formatDuration(duration = vm.duration),
                        audioProgressString = vm.onGoingProgressString,
                        modifier = Modifier,
                        playResourceProvider = if (vm.isPlaying) R.drawable.ic_pause else R.drawable.ic_play,
                        progressProvider = {
                            Pair(vm.progress, vm.progressString)
                        },
                        currentMediaIndex = vm.currentMediaIndex,
                        isLoading = vm.isLoading,
                        durationList = vm.durationList,
                        isReady = vm.isReady,
                    )
                    SenderAudioItem(
                        senderName = "TZO",
                        onUIEvent = vm::onUiEvent,
                        durationString = vm.formatDuration(duration = vm.duration),
                        audioProgressString = vm.onGoingProgressString,
                        modifier = Modifier,
                        playResourceProvider = if (vm.isPlaying) R.drawable.ic_pause else R.drawable.ic_play,
                        progressProvider = {
                            Pair(vm.progress, vm.progressString)
                        },
                        currentMediaIndex = vm.currentMediaIndex,
                        isLoading = vm.isLoading,
                        durationList = vm.durationList,
                        isReady = vm.isReady,
                    )
                }
            }
        })
}