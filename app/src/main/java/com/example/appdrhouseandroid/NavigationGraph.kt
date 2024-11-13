package com.example.appdrhouseandroid

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable



@Composable
fun NavigationGraph(navController: NavHostController, onBottomBarVisibilityChanged: (Boolean) -> Unit) {
    NavHost(navController, startDestination = Routes.Home.route) {

        composable(Routes.Home.route) {
            onBottomBarVisibilityChanged(true)
            Home()
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
            onBottomBarVisibilityChanged(false)
            Login(navController)
        }
        composable(Routes.SignUp.route) {
            onBottomBarVisibilityChanged(false)
            SignUp(navController)
        }
    }
}
