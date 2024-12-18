package com.example.appdrhouseandroid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun BottomBar(
    navController: NavController,
    state: Boolean,
    modifier: Modifier = Modifier
) {
    val screens = listOf(
        BottomNavigationItems.Home,
        BottomNavigationItems.ProductView,
        BottomNavigationItems.PredectionScreen,
        BottomNavigationItems.ProgressScreen,
        BottomNavigationItems.GoalReminder
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    spotColor = Color.Black.copy(alpha = 0.15f),
                    clip = false
                ),
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                screens.forEachIndexed { index, screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                    when (index) {
                        2 -> { // Center item (PredectionScreen)
                            CenterNavigationItem(
                                screen = screen,
                                selected = selected,
                                onItemClick = {
                                    navigateToTab(navController, screen.route)
                                }
                            )
                        }
                        else -> {
                            RegularTabItem(
                                screen = screen,
                                currentRoute = currentDestination?.route,
                                onItemClick = {
                                    navigateToTab(navController, screen.route)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun navigateToTab(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
            // Clear back stack for Profile and HealthReminder
            if (route == Routes.Profile.route || route == Routes.HealthReminder.route) {
                inclusive = true
            }
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun CenterNavigationItem(
    screen: BottomNavigationItems,
    selected: Boolean,
    onItemClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp)
    ) {
        Box(
            modifier = Modifier
                .offset(y = (-10).dp)
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF2980B9))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onItemClick,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = screen.icon!!,
                    contentDescription = screen.title,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Text(
            text = screen.title ?: "",
            color = if (selected) Color(0xFF2980B9) else Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier
                .offset(y = (-4).dp)
                .width(72.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RegularTabItem(
    screen: BottomNavigationItems,
    currentRoute: String?,
    onItemClick: () -> Unit
) {
    val selected = currentRoute == screen.route

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp)
    ) {
        IconButton(
            onClick = onItemClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = screen.icon!!,
                contentDescription = screen.title,
                tint = if (selected) Color(0xFF2980B9) else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = screen.title ?: "",
            color = if (selected) Color(0xFF2980B9) else Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.width(72.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BottomBarPreview() {
    val navController = rememberNavController()
    BottomBar(navController = navController, state = true)
}