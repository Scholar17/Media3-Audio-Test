package com.example.media3audiotest.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.media3audiotest.R

@Composable
fun PlayerControls(
    playResourceProvider: Int,
    onUIEvent: (PlayerEvent) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = R.drawable.ic_previous),
            contentDescription = "go to previous",
            modifier = Modifier
                .clickable(onClick = { onUIEvent(PlayerEvent.Backward) })
                .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
                .size(24.dp)
        )

        Icon(
            painter = painterResource(id = playResourceProvider),
            contentDescription = "play or pause",
            modifier = Modifier
                .clickable(onClick = { onUIEvent(PlayerEvent.PlayPause) })
                .padding(12.dp)
                .size(24.dp)
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_next), contentDescription = "go to next",
            modifier = Modifier
                .clickable(onClick = { onUIEvent(PlayerEvent.Forward) })
                .padding(12.dp)
                .size(24.dp)
        )
    }
}

@Preview
@Composable
fun PlayerControlsPrev() {
    PlayerControls(playResourceProvider = R.drawable.ic_play, onUIEvent = {})
}