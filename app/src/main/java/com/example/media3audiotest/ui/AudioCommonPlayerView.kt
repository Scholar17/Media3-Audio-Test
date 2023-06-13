package com.example.media3audiotest.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.media3audiotest.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioCommonPlayerView(
    durationString: String,
    playResourceProvider: Int,
    progressProvider: () -> Pair<Float, String>,
    onUIEvent: (PlayerEvent) -> Unit,
    modifier: Modifier
) {
    val (progress, progressString) = progressProvider()

    Surface {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .shadow(
                        elevation = 4.dp, shape = RoundedCornerShape(8.dp)
                    )
                    .background(Color.LightGray)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PlayerBar(
                        progress = progress,
                        durationString = durationString,
                        progressString = progressString,
                        onUIEvent = onUIEvent
                    )
                    PlayerControls(
                        playResourceProvider = playResourceProvider,
                        onUIEvent = onUIEvent
                    )
                }
            }
        }
    }

}

@Preview()
@Composable
fun AudioCommonPlayerPreview() {
    AudioCommonPlayerView(
        durationString = "12:06",
        playResourceProvider = R.drawable.ic_play,
        progressProvider = { Pair(0.1f, "12:06") },
        onUIEvent = {},
        modifier = Modifier
    )
}