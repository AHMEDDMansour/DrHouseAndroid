package com.example.appdrhouseandroid

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.appdrhouseandroid.ID.ProductViewModelFactory
import com.example.appdrhouseandroid.data.network.RetrofitClient
import com.example.appdrhouseandroid.data.repositories.OcrRepository
import com.example.appdrhouseandroid.data.repositories.ProductRepository
import com.example.appdrhouseandroid.data.repositories.UserRepository
import com.example.appdrhouseandroid.ui.signup.SignUpViewModel
import com.example.appdrhouseandroid.ui.theme.OCR.OCRScreen
import com.example.appdrhouseandroid.ui.theme.OCR.OCRViewModel
import com.example.appdrhouseandroid.ui.theme.Screen1
import com.example.appdrhouseandroid.ui.theme.forgetpassword.ConfirmCodeScreen
import com.example.appdrhouseandroid.ui.theme.forgetpassword.ForgetPassword
import com.example.appdrhouseandroid.ui.theme.forgetpassword.ResetPass
import com.example.appdrhouseandroid.ui.theme.login.Login
import com.example.appdrhouseandroid.ui.theme.login.LoginViewModel
import com.example.appdrhouseandroid.ui.theme.product.CheckoutScreen
import com.example.appdrhouseandroid.ui.theme.product.OCRViewModelFactory
import com.example.appdrhouseandroid.ui.theme.signup.SignUp
import com.example.appdrhouseandroid.ui.theme.product.ProductView
import com.example.appdrhouseandroid.ui.theme.product.ProductViewModel


//@Composable
//fun NavigationGraph(navController: NavHostController, onBottomBarVisibilityChanged: (Boolean) -> Unit) {
//    NavHost(navController, startDestination = Routes.ProductView.route) {
//
//
//        composable(Routes.Home.route) {
//            onBottomBarVisibilityChanged(true)
//            val apiService = RetrofitClient.getApiService()
//            val userRepository = UserRepository(apiService)
//            val loginViewModel = LoginViewModel(userRepository)
//            Home(navController, loginViewModel)
//        }
//        composable(Routes.AIscreen.route) {
//            onBottomBarVisibilityChanged(true)
//            AIscreen()
//        }
//        composable(Routes.Favorite.route) {
//            onBottomBarVisibilityChanged(true)
//            Favorite()
//        }
//        composable(Routes.Reminder.route) {
//            onBottomBarVisibilityChanged(true)
//            Reminder()
//        }
//        composable(Routes.Screen1.route) {
//            onBottomBarVisibilityChanged(false)
//            Screen1()
//        }
//        composable(Routes.LunchScreen.route) {
//            onBottomBarVisibilityChanged(false)
//            LunchScreen(navController)
//        }
//        composable(Routes.ConfirmCode.route) {
//            onBottomBarVisibilityChanged(false)
//            ConfirmCodeScreen(navController)
//        }
//        composable(Routes.ForgetPassword.route) {
//            onBottomBarVisibilityChanged(false)
//            ForgetPassword(navController)
//        }
//        composable(Routes.ResetPass.route) {
//            onBottomBarVisibilityChanged(false)
//            ResetPass(navController)
//        }
//        composable(Routes.Login.route) {
//            val apiService = RetrofitClient.getApiService()
//            val userRepository = UserRepository(apiService)
//            val loginViewModel = LoginViewModel(userRepository)
//            onBottomBarVisibilityChanged(false)
//            Login(navController,loginViewModel)
//        }
//        composable(Routes.SignUp.route) {
//            val apiService = RetrofitClient.getApiService()
//            val userRepository = UserRepository(apiService)
//            val signUpViewModel = SignUpViewModel(userRepository)
//            onBottomBarVisibilityChanged(false)
//            SignUp(navController,signUpViewModel)
//        }
//
//        composable(Routes.OCRScreen.route) {
//            onBottomBarVisibilityChanged(false)
//
//            // Initialize the ApiService and Repository
//            val apiService = RetrofitClient.getApiService()
//            val ocrRepository = OcrRepository(apiService)
//
//            // Create the OCRViewModel with the repository
//            val ocrViewModel = OCRViewModel(ocrRepository)
//
//            // Pass the navController and OCRViewModel to the OCRScreen composable
//            OCRScreen(navController, ocrViewModel)
//        }
//
//
//        composable(Routes.ProductView.route) {
//            onBottomBarVisibilityChanged(true)
//
//            val productRepository = ProductRepository(RetrofitClient.getApiService())
//            val productViewModel: ProductViewModel = viewModel(
//                factory = ProductViewModelFactory(productRepository)
//            )
//
//            val ocrRepository = OcrRepository(RetrofitClient.getApiService())
//            val ocrViewModel: OCRViewModel = viewModel(
//                factory = OCRViewModelFactory(ocrRepository)
//            )
//
//            ProductView(
//                navController = navController,
//                viewModel = productViewModel,
//                ocrViewModel = ocrViewModel
//            )
//        }
//
//        composable(Routes.CartView.route) {
//            onBottomBarVisibilityChanged(false)
//            val productRepository = ProductRepository(RetrofitClient.getApiService())
//            val sharedProductViewModel: ProductViewModel = viewModel(
//                factory = ProductViewModelFactory(productRepository)
//            )
//            CartView(
//                cartViewModel = sharedProductViewModel,
//                onProceedToCheckout = {
//                    // Navigate to the checkout screen or perform an action
//                    navController.navigate(Routes.ProductView.route)
//                }
//            )
//        }
//
//
//
//    }
//}

@Composable
fun NavigationGraph(navController: NavHostController, onBottomBarVisibilityChanged: (Boolean) -> Unit) {
    // Initialize common dependencies
    val apiService = RetrofitClient.getApiService()
    val userRepository = UserRepository(apiService)
    val productRepository = ProductRepository(apiService)
    val ocrRepository = OcrRepository(apiService)

    // Share ViewModel instances across composables
    val sharedProductViewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(productRepository)
    )
    val sharedOcrViewModel: OCRViewModel = viewModel(
        factory = OCRViewModelFactory(ocrRepository)
    )

    NavHost(navController, startDestination = Routes.ProductView.route) {
        composable(Routes.Home.route) {
            onBottomBarVisibilityChanged(true)
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
            onBottomBarVisibilityChanged(false)
            Screen1()
        }
        composable(Routes.LunchScreen.route) {
            onBottomBarVisibilityChanged(false)
            LunchScreen(navController)
        }
        composable(Routes.ConfirmCode.route) {
            onBottomBarVisibilityChanged(false)
            ConfirmCodeScreen(navController)
        }
        composable(Routes.ForgetPassword.route) {
            onBottomBarVisibilityChanged(false)
            ForgetPassword(navController)
        }
        composable(Routes.ResetPass.route) {
            onBottomBarVisibilityChanged(false)
            ResetPass(navController)
        }
        composable(Routes.Login.route) {
            onBottomBarVisibilityChanged(false)
            val loginViewModel = LoginViewModel(userRepository)
            Login(navController, loginViewModel)
        }
        composable(Routes.SignUp.route) {
            onBottomBarVisibilityChanged(false)
            val signUpViewModel = SignUpViewModel(userRepository)
            SignUp(navController, signUpViewModel)
        }
        composable(Routes.OCRScreen.route) {
            onBottomBarVisibilityChanged(false)
            OCRScreen(navController, sharedOcrViewModel)
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









    }
}
