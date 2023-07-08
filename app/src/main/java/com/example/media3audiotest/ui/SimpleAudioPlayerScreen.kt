package com.example.media3audiotest.ui

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.media3audiotest.MediaViewModel
import com.example.media3audiotest.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleAudioPlayerScreen(
    vm: MediaViewModel,
    startService: () -> Unit,
) {
    val state = vm.uiState.collectAsState()

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