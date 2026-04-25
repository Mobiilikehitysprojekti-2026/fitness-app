package com.example.fitnessapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home     : BottomNavItem(ROUTE_HOME,     Icons.Default.Home,        "Home")
    object Workouts : BottomNavItem(ROUTE_WORKOUTS_LIST, Icons.Default.Menu,"Workouts")
    object Profile  : BottomNavItem(ROUTE_PROFILE,  Icons.Default.Person,      "Profile")
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Workouts,
    BottomNavItem.Profile
)