package com.drabatx.chatio.presentation.navigation

import androidx.annotation.StringRes
import com.drabatx.chatio.R

sealed class AppScreens(val route: String, @StringRes val resourceId: Int) {
    data object SplashScreen: AppScreens("splash_screen", R.string.splash_screen)
    data object LoginScreen: AppScreens("login_screen", R.string.login_screen)
    data object ChatScreen: AppScreens("chat_screen", R.string.chat_screen)
}