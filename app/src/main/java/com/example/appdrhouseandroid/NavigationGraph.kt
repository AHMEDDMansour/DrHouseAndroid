package com.example.appdrhouseandroid

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.appdrhouseandroid.ID.ProductViewModelFactory
import com.example.appdrhouseandroid.data.network.RetrofitClient
import com.example.appdrhouseandroid.data.repositories.OcrRepository
import com.example.appdrhouseandroid.data.repositories.PredictionRepository
import com.example.appdrhouseandroid.data.repositories.ProductRepository
import com.example.appdrhouseandroid.data.repositories.ProgressRepository
import com.example.appdrhouseandroid.data.repositories.UserRepository
import com.example.appdrhouseandroid.data.repository.GoalRepository
import com.example.appdrhouseandroid.ui.signup.SignUpViewModel
import com.example.appdrhouseandroid.ui.theme.AiPredection.PredictionScreen
import com.example.appdrhouseandroid.ui.theme.AiPredection.PredictionViewModel
import com.example.appdrhouseandroid.ui.theme.OCR.OCRViewModel
import com.example.appdrhouseandroid.ui.theme.Set_Goals.GoalSettingScreen
import com.example.appdrhouseandroid.ui.theme.Set_Goals.GoalSettingScreenViewModel
import com.example.appdrhouseandroid.ui.theme.Set_Goals.ShowGoalsScreen
import com.example.appdrhouseandroid.ui.theme.Set_Goals.UserGoals
import com.example.appdrhouseandroid.ui.theme.StepCounter.StepCounter
import com.example.appdrhouseandroid.ui.theme.WaterReminder.HealthReminder
import com.example.appdrhouseandroid.ui.theme.forgetpassword.PasswordResetScreen
import com.example.appdrhouseandroid.ui.theme.login.Login
import com.example.appdrhouseandroid.ui.theme.login.LoginViewModel
import com.example.appdrhouseandroid.ui.theme.product.CheckoutScreen
import com.example.appdrhouseandroid.ui.theme.product.OCRViewModelFactory
import com.example.appdrhouseandroid.ui.theme.product.ProductView
import com.example.appdrhouseandroid.ui.theme.product.ProductViewModel
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
    val productRepository = ProductRepository(apiService)
    val ocrRepository = OcrRepository(apiService)
    val sharedProductViewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(productRepository)
    )
    val sharedOcrViewModel: OCRViewModel = viewModel(
        factory = OCRViewModelFactory(ocrRepository)
    )
    val progressRepository = ProgressRepository(apiService, sharedPreferences)
    val stepCounter = remember { StepCounter(context) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(Unit) {
        lifecycle.addObserver(stepCounter)
        onDispose {
            lifecycle.removeObserver(stepCounter)
        }
    }
    // Initialize ViewModels
    val loginViewModel = LoginViewModel(userRepository)
    val signUpViewModel = SignUpViewModel(userRepository)
    val predictionViewModel = PredictionViewModel(predictionRepository)
    val goalsViewModel = GoalSettingScreenViewModel(goalsRepository)
    val progressViewModel = ProgressViewModel(progressRepository,stepCounter)

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
        composable(Routes.ProductView.route) {
            onBottomBarVisibilityChanged(true)
            ProductView(
                navController = navController,
                viewModel = sharedProductViewModel,
                ocrViewModel = sharedOcrViewModel
            )
        }
        composable(Routes.CheckoutScreen.route) {
            onBottomBarVisibilityChanged(false)

            CheckoutScreen(
                viewModel = sharedProductViewModel,
                onOrderSuccess = {
                    navController.navigate(Routes.ProductView.route) {
                        popUpTo(Routes.ProductView.route) { inclusive = false }
                    }
                },
                onOrderError = {
                }
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

        composable(
            route = Routes.HealthReminder.route,
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right) }
        ) {
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
        composable(Routes.Profile.route) {
            onBottomBarVisibilityChanged(true)
            Profile(
                navController = navController,
                loginViewModel = loginViewModel
            )
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