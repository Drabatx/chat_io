package com.drabatx.chatio.presentation.view.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.drabatx.chatio.R
import com.drabatx.chatio.presentation.view.theme.margin_big
import com.drabatx.chatio.presentation.view.theme.margin_medium

@Preview
@Composable
fun LoadingDialogPreview() {
    LoadingDialog(true)
}

@Composable
fun LoadingDialog(isLoading: Boolean) {
    var showDialog by remember { mutableStateOf(isLoading) }
    Dialog(
        onDismissRequest = { showDialog = false },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Box(
            contentAlignment = Center,
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .background(White, shape = RoundedCornerShape(8.dp))
        ) {
            Column(
                modifier = Modifier.padding(vertical = margin_big, horizontal = margin_medium)
            ) {
                AnimatedPreloader()
                Text(
                    text = stringResource(R.string.loading),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun AnimatedPreloader() {
    val preloaderLottie by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_anim))
    val preloaderProgress by animateLottieCompositionAsState(
        preloaderLottie,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )
    LottieAnimation(
        composition = preloaderLottie,
        iterations = Int.MAX_VALUE,
        modifier = Modifier
            .height(150.dp)
            .aspectRatio(1f)
    )
}

