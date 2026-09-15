package com.nexvary.andalus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

private val RoyalSystemBarColor = 0xFF071A46.toInt()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(RoyalSystemBarColor),
            navigationBarStyle = SystemBarStyle.dark(RoyalSystemBarColor),
        )

        setContent {
            RoyalAndalusStudioV3()
        }
    }
}
