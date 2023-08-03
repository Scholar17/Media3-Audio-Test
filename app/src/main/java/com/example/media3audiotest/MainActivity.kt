package com.example.media3audiotest

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.media3audiotest.ui.SimpleAudioPlayerScreen
import com.example.media3audiotest.ui.theme.Media3AudioTestTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MediaViewModel by viewModels()
    private var isServiceRunning = false
    
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Media3AudioTestTheme {
                // A surface container using the 'background' color from the theme
                SimpleAudioPlayerScreen(
                    vm = viewModel,
                    startService = ::startService,
                )
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun startService() {
        if (!isServiceRunning) {
            val intent = Intent(this, MediaService::class.java)
            startForegroundService(intent)
            isServiceRunning = true
        }
    }
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Media3AudioTestTheme {
        Greeting("Android")
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
