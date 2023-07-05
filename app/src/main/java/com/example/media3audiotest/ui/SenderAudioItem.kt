package com.example.media3audiotest.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.UiMode
import androidx.compose.ui.unit.dp
import com.example.media3audiotest.R

@Composable
fun SenderAudioItem(
    modifier: Modifier = Modifier,
    senderName: String,
    onUIEvent: (PlayerEvent) -> Unit,
    durationString: String,
    audioProgressString: String,
    @DrawableRes playResourceProvider: Int,
    progressProvider: () -> Pair<Float, String>,
    currentMediaIndex: Int
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
        horizontalAlignment = Alignment.End
    ) {
        Column(modifier = modifier.fillMaxWidth(fraction = 0.65f)) {
            Row(
                modifier = modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 12.dp,
                            bottomEnd = 12.dp,
                            bottomStart = 12.dp,
                        )
                    )
                    .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                    .clickable { }
                    .padding(
                        start = 8.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                    AudioPlayerView(
                        isSender = false,
                        durationString = durationString,
                        audioProgressString = durationString,
                        playResourceProvider = if(currentMediaIndex == 1) playResourceProvider else R.drawable.ic_play,
                        onUIEvent = onUIEvent,
                        progressProvider = if(currentMediaIndex == 1) progressProvider else {{ Pair(0f, "00:00") }},
                        audioIndex = 1
                    )
            }
            Text(
                modifier = modifier.padding(
                    top = 4.dp,
                    bottom = 16.dp
                ),
                text = if(currentMediaIndex == 1) audioProgressString else durationString,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Preview
@Composable
fun SenderAudioItemPrev() {
    SenderAudioItem(
        modifier = Modifier,
        senderName = "TZO",
        onUIEvent = {},
        durationString = "10:54",
        audioProgressString = "10:54",
        playResourceProvider = R.drawable.ic_play,
        progressProvider = {Pair(0.5f, "10:55") },
        currentMediaIndex = 1
    )
}