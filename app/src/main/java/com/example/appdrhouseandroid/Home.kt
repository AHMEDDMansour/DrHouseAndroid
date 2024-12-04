package com.example.appdrhouseandroid

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appdrhouseandroid.ui.theme.login.LoginViewModel

@Composable
fun Home(navController: NavHostController, loginViewModel: LoginViewModel) {
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Welcome to Home Screen!")

        Button(onClick = {
            loginViewModel.logout(context)
            navController.navigate(Routes.Login.route) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }) {
            Text(text = "Logout")
        }
    }
}