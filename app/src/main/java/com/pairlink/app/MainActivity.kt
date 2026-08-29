package com.pairlink.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.pairlink.app.core.designsystem.DesignTokens
import com.pairlink.app.core.designsystem.PairLinkTheme
import com.pairlink.app.ui.navigation.NavGraph
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single-Activity entry point for PairLink.
 * Hosts the Jetpack Compose navigation graph and design system theme.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
                PairLinkTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DesignTokens.Colors.BackgroundMidnight
                ) {
                    NavGraph()
                }
            }
        }
    }
}
