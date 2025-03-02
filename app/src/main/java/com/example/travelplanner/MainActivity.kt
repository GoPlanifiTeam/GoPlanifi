package com.example.travelplanner

import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.travelplanner.ui.theme.TestingTheme
import kotlinx.coroutines.delay
import android.widget.ImageView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            var showSplash by remember { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                delay(3500) // Show GIF for 3 seconds
                showSplash = false // Start fading out
            }

            TestingTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    MainScreen() // Load MainScreen immediately so that I don't have that little delay
                    AnimatedVisibility(
                        visible = showSplash,
                        exit = fadeOut(animationSpec = tween(200)) // 1s fade-out
                    ) {
                        SplashScreen(onSplashFinished = { showSplash = false })
                    }
                }
            }
        }
    }
}


@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    var isVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(3000) // Show GIF for 3 seconds
        isVisible = false // Start fade-out transition (1s)
        delay(1000) // Wait for fade animation to complete
        onSplashFinished() // Notify that splash is finished
    }

    AnimatedVisibility(
        visible = isVisible,
        exit = fadeOut(animationSpec = tween(1000)) // 1s fade-out
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    ImageView(context).apply {
                        scaleType = ImageView.ScaleType.FIT_XY

                        val gifDrawable: Drawable? = ResourcesCompat.getDrawable(
                            context.resources, R.drawable.goplanifi, null
                        )

                        if (gifDrawable is AnimatedImageDrawable) {
                            gifDrawable.start()
                        }

                        setImageDrawable(gifDrawable)
                    }
                }
            )
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    NavGraph(navController = navController)
}