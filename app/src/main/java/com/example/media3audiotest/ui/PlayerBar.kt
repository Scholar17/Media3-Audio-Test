package com.example.media3audiotest.ui

import android.widget.SeekBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player

@Composable
fun PlayerBar(
    progress: Float,
    durationString: String,
    progressString: String,
    onUIEvent: (PlayerEvent) -> Unit
) {
    val newProgressValue = remember { mutableStateOf(0f) }
    val useNewProgressValue = remember { mutableStateOf(false) }

    Column {
        Slider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            value = if (useNewProgressValue.value) newProgressValue.value else progress,
            onValueChange = { newValue ->
                useNewProgressValue.value = true
                newProgressValue.value = newValue
                onUIEvent(PlayerEvent.UpdateProgress(newProgress = newValue))
            },
            onValueChangeFinished = { useNewProgressValue.value = false },
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = progressString, style = MaterialTheme.typography.bodySmall)
            Text(text = durationString, style = MaterialTheme.typography.bodySmall)
        }
    }
}