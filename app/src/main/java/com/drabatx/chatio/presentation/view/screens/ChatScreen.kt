package com.drabatx.chatio.presentation.view.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.drabatx.chatio.presentation.view.widgets.MainTopBar

@Composable
fun ChatScreen() {
    Scaffold(topBar = { MainTopBar() }, content = { innerPadding ->
        Text(text = "Chat Screen", modifier = Modifier.padding(innerPadding))
    })
}

