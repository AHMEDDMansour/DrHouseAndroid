package com.example.appdrhouseandroid

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.appdrhouseandroid.data.network.RetrofitClient
import com.example.appdrhouseandroid.data.repositories.PredictionRepository
import com.example.appdrhouseandroid.data.repositories.ProgressRepository
import com.example.appdrhouseandroid.data.repositories.UserRepository
import com.example.appdrhouseandroid.data.repository.GoalRepository
import com.example.appdrhouseandroid.ui.signup.SignUpViewModel
import com.example.appdrhouseandroid.ui.theme.AiPredection.PredictionScreen
import com.example.appdrhouseandroid.ui.theme.AiPredection.PredictionViewModel
import com.example.appdrhouseandroid.ui.theme.Set_Goals.GoalSettingScreen
import com.example.appdrhouseandroid.ui.theme.Set_Goals.GoalSettingScreenViewModel
import com.example.appdrhouseandroid.ui.theme.Set_Goals.ShowGoalsScreen
import com.example.appdrhouseandroid.ui.theme.Set_Goals.UserGoals
import com.example.appdrhouseandroid.ui.theme.WaterReminder.HealthReminder
import com.example.appdrhouseandroid.ui.theme.forgetpassword.PasswordResetScreen
import com.example.appdrhouseandroid.ui.theme.login.Login
import com.example.appdrhouseandroid.ui.theme.login.LoginViewModel
import com.example.appdrhouseandroid.ui.theme.proggres.ProgressScreen
import com.example.appdrhouseandroid.ui.theme.proggres.ProgressViewModel
import com.example.appdrhouseandroid.ui.theme.reminder.GoalReminder
import com.example.appdrhouseandroid.ui.theme.signup.SignUp


@RequiresApi(Build.VERSION_CODES.O)
@Composable

fun NavigationGraph(navController: NavHostController, onBottomBarVisibilityChanged: (Boolean) -> Unit) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    // Initialize API Service
    val apiService = RetrofitClient.getApiService()

    // Initialize Repositories
    val userRepository = UserRepository(apiService)
    val predictionRepository = PredictionRepository(apiService)
    val goalsRepository = GoalRepository(apiService)
    val progressRepository = ProgressRepository(apiService, sharedPreferences)

    // Initialize ViewModels
    val loginViewModel = LoginViewModel(userRepository)
    val signUpViewModel = SignUpViewModel(userRepository)
    val predictionViewModel = PredictionViewModel(predictionRepository)
    val goalsViewModel = GoalSettingScreenViewModel(goalsRepository)
    val progressViewModel = ProgressViewModel(progressRepository)

    NavHost(navController, startDestination = Routes.LunchScreen.route) {
        composable(Routes.Home.route) {
            onBottomBarVisibilityChanged(true)
            Home(navController, loginViewModel)
        }

        composable(Routes.ProgressScreen.route) {
            onBottomBarVisibilityChanged(true)

            ProgressScreen(
                progressViewModel = progressViewModel,
                goalViewModel = goalsViewModel
            )
        }
        composable(Routes.GoalSettingScreen.route) {
            onBottomBarVisibilityChanged(false)
            GoalSettingScreen(
                onGoalsSaved = {
                    navController.popBackStack()
                },
                viewModel = goalsViewModel
            )
        }

        composable(Routes.AIscreen.route) {
            onBottomBarVisibilityChanged(true)
            AIscreen()
        }
                composable(Routes.GoalReminder.route) {
                    onBottomBarVisibilityChanged(true)
                    GoalReminder(goalsViewModel)
                }

        composable(Routes.Favorite.route) {
            onBottomBarVisibilityChanged(true)
            Favorite()
        }

        composable(Routes.HealthReminder.route) {
            onBottomBarVisibilityChanged(true)
            HealthReminder()
        }

        composable(Routes.Screen1.route) {
            onBottomBarVisibilityChanged(true)
            Screen1()
        }

        composable(Routes.LunchScreen.route) {
            onBottomBarVisibilityChanged(false)
            LunchScreen(navController)
        }


        composable(Routes.ForgetPassword.route) {
            onBottomBarVisibilityChanged(false)
            PasswordResetScreen(navController)
        }

        composable(Routes.PredictionScreen.route) {
            onBottomBarVisibilityChanged(true)
            PredictionScreen(predictionViewModel)
        }



        composable(Routes.Login.route) {
            onBottomBarVisibilityChanged(false)
            Login(navController, loginViewModel)
        }

        composable(Routes.SignUp.route) {
            onBottomBarVisibilityChanged(false)
            SignUp(navController, signUpViewModel)
        }
    }
}