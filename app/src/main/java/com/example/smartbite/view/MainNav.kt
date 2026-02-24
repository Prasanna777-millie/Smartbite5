package com.example.smartbite.view

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartbite.ui.view.ProfileScreen

@Composable
fun MainNav(
    isDarkMode: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        composable("home") {
            HomeScreen(navController)
        }

        composable("details/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
            DetailsScreen(itemId = itemId)
        }

        composable("notifications") {
            NotificationScreen()
        }

        composable("cart") {
            CartScreen()
        }

        composable("profile") {
            ProfileScreen(
                isDarkMode = isDarkMode,
                onThemeChange = onThemeChange,

                onBack = { navController.popBackStack() },
                onEditProfile = { /* navController.navigate("editProfile") */ },
                onMyOrders = { /* navController.navigate("myOrders") */ },
                onPaymentHistory = { /* navController.navigate("paymentHistory") */ },
                onHelpSupport = { /* navController.navigate("help") */ },
                onLogout = { /* navController.navigate("login") */ }
            )
        }
    }
}