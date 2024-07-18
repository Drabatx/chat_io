package com.drabatx.chatio.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.drabatx.chatio.presentation.view.screens.ChatScreen
import com.drabatx.chatio.presentation.view.screens.LoginScreen
import com.drabatx.chatio.presentation.view.screens.SplashScreen
import com.drabatx.chatio.presentation.viewmodels.ChatViewModel
import com.drabatx.chatio.presentation.viewmodels.LoginViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = viewModel()
    val chatViewModel: ChatViewModel = viewModel()
    NavHost(navController = navController, startDestination = AppScreens.SplashScreen.route) {
        composable(route = AppScreens.SplashScreen.route) {
            SplashScreen(navController = navController)
        }
        composable(route = AppScreens.LoginScreen.route) {
            LoginScreen(navController = navController, loginViewModel = loginViewModel)
        }
        composable(route = AppScreens.ChatScreen.route) {
            ChatScreen(navController, chatViewModel)
        }
    }
}