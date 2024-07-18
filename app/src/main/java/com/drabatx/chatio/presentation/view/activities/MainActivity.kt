package com.drabatx.chatio.presentation.view.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.drabatx.chatio.presentation.navigation.AppNavigation
import com.drabatx.chatio.presentation.view.theme.ChatIoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            ChatIoTheme {
                AppNavigation()
            }
        }
    }
}
