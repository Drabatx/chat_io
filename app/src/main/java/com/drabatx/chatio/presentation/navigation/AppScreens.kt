package com.drabatx.chatio.presentation.navigation

import androidx.annotation.StringRes
import com.drabatx.chatio.R

sealed class AppScreens(val route: String, @StringRes val resourceId: Int) {
    data object LoginScreen: AppScreens("login_screen", R.string.login_screen)
    data object ChatScreen: AppScreens("chat_screen", R.string.chat_screen)
//    data object AnimeFull: AppScreens("anime_full/{animeId}", R.string.menu_anime_full) {
//        fun setAnimeId(animeId: Int) = "anime_full/$animeId"
//    }
}