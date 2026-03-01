package com.example.smartbite.view

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainNav(
    isDarkMode: Boolean,
    onThemeChange: (Boolean) -> Unit,
    currentUserId: String
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        composable("home") {
            Homescreen(navController)
        }

        composable("details/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
            DetailsScreen(itemId = itemId)
        }

        composable("notifications") {
            NotificationScreen(
                navController = navController,
                currentUserId = currentUserId
            )
        }

        composable("cart") {
            CartScreen()
        }

        composable("profile") {
            ProfileScreenNew(
                isDarkMode = isDarkMode,
                onThemeChange = onThemeChange,
                onBack = { navController.popBackStack() },
                onEditProfile = { },
                onMyOrders = { },
                onPaymentHistory = { },
                onHelpSupport = { },
                onLogout = { }
            )
        }
    }
}