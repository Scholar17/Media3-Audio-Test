package com.example.media3audiotest.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.media3audiotest.R

@Composable
fun ReceiverAudioItem(
    modifier: Modifier = Modifier,
    senderName: String,
    onUIEvent: (PlayerEvent) -> Unit,
    durationString: String,
    audioProgressString: String,
    currentMediaIndex: Int,
    @DrawableRes playResourceProvider: Int,
    progressProvider: () -> Pair<Float, String>,
    isLoading: Boolean,
    isReady: Boolean,
    durationList: List<String>,
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp, vertical = 8.dp
            ), horizontalAlignment = Alignment.Start
    ) {
        Column(modifier = modifier.fillMaxWidth(fraction = 0.65f)) {
            Row(
                modifier = modifier
                    .clip(
                        RoundedCornerShape(
                            topEnd = 12.dp,
                            bottomEnd = 12.dp,
                            bottomStart = 12.dp,
                        )
                    )
                    .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                    .clickable { }
                    .padding(
                        start = 8.dp, end = 16.dp, top = 8.dp, bottom = 8.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AudioPlayerView(
                    isSender = false,
                    durationString = durationString,
                    playResourceProvider = if (currentMediaIndex == 0) playResourceProvider else R.drawable.ic_play,
                    onUIEvent = onUIEvent,
                    progressProvider = if (currentMediaIndex == 0) progressProvider else {
                        { Pair(0f, "00:00") }
                    },
                    audioProgressString = if (currentMediaIndex == 1 && isReady) audioProgressString else durationList[1],
                    audioIndex = 0,
                    isReady = if (currentMediaIndex == 0) isReady else true,
                    isLoading = if (currentMediaIndex == 0) isLoading else false
                )
            }
            Text(
                modifier = modifier.padding(
                    top = 4.dp, bottom = 16.dp
                ),
                text = if (currentMediaIndex == 0 && isReady) audioProgressString else durationList[0],
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun VerticalSpacerSmall() {
    Spacer(modifier = Modifier.height(8.dp))
}

@Preview
@Composable
fun ReveiverAudioItemPrev() {
    ReceiverAudioItem(
        modifier = Modifier,
        senderName = "TZO",
        onUIEvent = {},
        durationString = "10:54",
        audioProgressString = "10:00",
        playResourceProvider = R.drawable.ic_play,
        progressProvider = { Pair(0.5f, "10:55") },
        currentMediaIndex = 0,
        isLoading = true,
        durationList = listOf("00:00", "01:00"),
        isReady = true
    )
}
