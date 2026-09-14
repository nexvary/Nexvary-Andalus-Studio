package com.nexvary.andalus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

private val RoyalSystemBarColor = 0xFFFFFBF5.toInt()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = RoyalSystemBarColor,
                darkScrim = RoyalSystemBarColor,
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = RoyalSystemBarColor,
                darkScrim = RoyalSystemBarColor,
            ),
        )

        setContent {
            AndalusStudioAppV2()
        }
    }
}
