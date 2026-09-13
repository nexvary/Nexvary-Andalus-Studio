package com.nexvary.andalus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

private const val RoyalSystemBarColor = 0xFFFFFBF5.toInt()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Royal UI uses a light cream surface behind the Android system bars.
        // Force dark status/navigation icons so time, signal and battery remain
        // readable on both the emulator evidence and real devices.
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
            RoyalAndalusTheme {
                RoyalStudioApp()
            }
        }
    }
}
