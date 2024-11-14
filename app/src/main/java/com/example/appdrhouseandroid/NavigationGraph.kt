package com.example.appdrhouseandroid

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.appdrhouseandroid.data.network.RetrofitClient
import com.example.appdrhouseandroid.data.repositories.UserRepository
import com.example.appdrhouseandroid.ui.signup.SignUpViewModel
import com.example.appdrhouseandroid.ui.theme.login.Login
import com.example.appdrhouseandroid.ui.theme.login.LoginViewModel
import com.example.appdrhouseandroid.ui.theme.signup.SignUp


@Composable
fun NavigationGraph(navController: NavHostController, onBottomBarVisibilityChanged: (Boolean) -> Unit) {
    NavHost(navController, startDestination = Routes.Login.route) {

        composable(Routes.Home.route) {
            onBottomBarVisibilityChanged(true)
            val apiService = RetrofitClient.getApiService()
            val userRepository = UserRepository(apiService)
            val loginViewModel = LoginViewModel(userRepository)
            Home(navController, loginViewModel)
        }
        composable(Routes.AIscreen.route) {
            onBottomBarVisibilityChanged(true)
            AIscreen()
        }
        composable(Routes.Favorite.route) {
            onBottomBarVisibilityChanged(true)
            Favorite()
        }
        composable(Routes.Reminder.route) {
            onBottomBarVisibilityChanged(true)
            Reminder()
        }
        composable(Routes.Screen1.route) {
            onBottomBarVisibilityChanged(true)
            Screen1()
        }
        composable(Routes.LunchScreen.route) {
            onBottomBarVisibilityChanged(false)
            LunchScreen(navController)
        }
        composable(Routes.Login.route) {
            val apiService = RetrofitClient.getApiService()
            val userRepository = UserRepository(apiService)
            val loginViewModel = LoginViewModel(userRepository)
            onBottomBarVisibilityChanged(false)
            Login(navController,loginViewModel)
        }
        composable(Routes.SignUp.route) {
            val apiService = RetrofitClient.getApiService()
            val userRepository = UserRepository(apiService)
            val signUpViewModel = SignUpViewModel(userRepository)
            onBottomBarVisibilityChanged(false)
            SignUp(navController,signUpViewModel)
        }
    }
}