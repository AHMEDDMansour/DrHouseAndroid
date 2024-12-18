package com.example.appdrhouseandroid

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Api
import androidx.compose.material.icons.outlined.CropPortrait
import androidx.compose.material.icons.outlined.Diamond
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavigationItems (

    val route: String,
    val title: String? = null,
    val icon: ImageVector? = null

){
    object Home : BottomNavigationItems(
        route = "Home",
        title = "Home",
        icon = Icons.Outlined.Home
    )

    object ProductView : BottomNavigationItems(
        route = "ProductView",
        title = "Products",
        icon = Icons.Outlined.ShoppingBasket
    )

    object PredectionScreen : BottomNavigationItems(
        route = "PredictionScreen",
        title = "AIscreen",
        icon = Icons.Outlined.Api
    )

    object ProgressScreen : BottomNavigationItems(
        route = "ProgressScreen",
        title = "LifeStyle",
        icon = Icons.Outlined.Diamond
    )

    object GoalReminder : BottomNavigationItems(
        route = "Profile",
        title = "Profile",
        icon = Icons.Outlined.Favorite
    )
}