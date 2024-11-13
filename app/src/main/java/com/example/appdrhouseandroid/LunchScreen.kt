package com.example.appdrhouseandroid

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.PopUpToBuilder
import kotlinx.coroutines.delay

@Composable
fun LunchScreen(navController: NavHostController){

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Green),
        contentAlignment = Alignment.Center
    ) {
        Image(  painter = painterResource(id = R.drawable.logo),
            contentDescription = "ImageProfile",
            modifier = Modifier.fillMaxSize())

    }
    LaunchedEffect(Unit) {
        delay(3000L) // Delay for 3 seconds
        navController.navigate(Routes.SignUp.route) {
            popUpTo(navController.graph.findStartDestination().id) {  saveState = true }
        }
    }
}
