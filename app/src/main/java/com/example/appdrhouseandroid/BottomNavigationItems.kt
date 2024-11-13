package com.example.appdrhouseandroid

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Api
import androidx.compose.material.icons.outlined.CropPortrait
import androidx.compose.material.icons.outlined.Diamond
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
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

    object Reminder : BottomNavigationItems(
        route = "Reminder",
        title = "Reminder",
        icon = Icons.Outlined.Notifications
    )

    object AIscreen : BottomNavigationItems(
        route = "AIscreen",
        title = "AIscreen",
        icon = Icons.Outlined.Api
    )

    object Screen1 : BottomNavigationItems(
        route = "Screen1",
        title = "Screen1",
        icon = Icons.Outlined.Diamond
    )

    object Favorite : BottomNavigationItems(
        route = "Favorite",
        title = "Favorite",
        icon = Icons.Outlined.Favorite
    )
}