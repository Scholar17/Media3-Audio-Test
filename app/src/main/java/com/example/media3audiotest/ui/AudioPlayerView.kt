package com.example.media3audiotest.ui

import android.widget.SeekBar
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.media3audiotest.R
import java.nio.file.WatchEvent


@Composable
fun AudioPlayerView(
    isSender: Boolean,
    durationString: String,
    audioProgressString: String,
    @DrawableRes playResourceProvider: Int,
    progressProvider: () -> Pair<Float, String>,
    onUIEvent: (PlayerEvent) -> Unit,
    modifier: Modifier = Modifier,
    audioUrl: String,
    isReady: Boolean,
    isLoading: Boolean,
) {

    val (progress, progressString) = progressProvider()


    Row(
        modifier.background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(modifier = Modifier.align(Alignment.CenterVertically)) {

            if (!isReady) {
                Box(modifier.size(50.dp).align(Alignment.Center)) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error,
                        strokeWidth = 1.dp,
                    )
                }
            } else {
                IconButton(
                    modifier = modifier
                        .size(50.dp),
                    onClick = { onUIEvent(PlayerEvent.PlayPause(audioUrl = audioUrl)) },
                    content = {
                        Icon(
                            painter = painterResource(id = playResourceProvider),
                            contentDescription = "pause button",
                            modifier = Modifier
                                .size(24.dp),
                            tint = if (isSender) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
                CircularProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .height(50.dp)
                        .width(50.dp),
                    color = MaterialTheme.colorScheme.error,
                    strokeWidth = 1.dp,
                )
            }


        }
        PlayerBar(
            progress = progress,
            durationString = durationString,
            progressString = progressString,
            onUIEvent = onUIEvent
        )
//        Text(
//            text = "00:00",
//            style = MaterialTheme.typography.labelMedium,
//            color = if (isSender) MaterialTheme.colorScheme.onPrimary
//            else MaterialTheme.colorScheme.onSurface
//        )
    }

}

@Composable
fun HorizontalSpacerBase() {
    Spacer(modifier = Modifier.width(8.dp))
}

@Preview
@Composable
fun AudioPlayerViewPrev() {
    AudioPlayerView(
        isSender = false,
        durationString = "00:21",
        audioProgressString = "00:10",
        playResourceProvider = R.drawable.ic_play,
        progressProvider = { Pair(0.1f, "12:06") },
        onUIEvent = {},
        modifier = Modifier,
        audioUrl = "",
        isReady = true,
        isLoading = false,
    )
}