package com.example.smartbite.view

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartbite.R
import com.example.smartbite.ui.theme.SmartBiteTheme
import com.google.firebase.auth.FirebaseAuth

class UserDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            var isDarkMode by remember { mutableStateOf(false) }

            SmartBiteTheme(darkTheme = isDarkMode) {
                UserDashboardScreen(
                    isDarkMode = isDarkMode,
                    onThemeChange = { newMode -> isDarkMode = newMode }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboardScreen(
    isDarkMode: Boolean = false,
    onThemeChange: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as Activity

    // ✅ current user id for NotificationScreen (Firestore)
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    data class NavItem(val label: String, val icon: Int)

    var selectedIndex by remember { mutableStateOf(0) }
    var showEditProfile by remember { mutableStateOf(false) }
    var showMyOrders by remember { mutableStateOf(false) }

    val navItems = listOf(
        NavItem("Home", R.drawable.baseline_home_24),
        NavItem("Notifications", R.drawable.baseline_notifications_24),
        NavItem("Cart", R.drawable.baseline_shopping_cart_24),
        NavItem("Profile", R.drawable.baseline_person_24)
    )

    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SmartBite") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = { Icon(painterResource(id = item.icon), contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (showEditProfile) {
                EditProfileScreen(onBack = { showEditProfile = false })
            } else if (showMyOrders) {
                MyOrdersScreen(onBack = { showMyOrders = false })
            } else {
                when (selectedIndex) {
                0 -> {
                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {

                        composable("home") {
                            Homescreen(navController)
                        }

                        composable("details/{itemId}") { backStackEntry ->
                            val itemId =
                                backStackEntry.arguments?.getString("itemId") ?: ""

                            DetailsScreen(
                                itemId = itemId,
                                navController = navController
                            )
                        }
                    }

                }


                1 -> NotificationScreen(
                    navController = navController,
                    currentUserId = currentUserId
                )
                2 -> CartScreen()
                3 -> ProfileScreenNew(
                    isDarkMode = isDarkMode,
                    onThemeChange = onThemeChange,
                    onBack = { selectedIndex = 0 },
                    onEditProfile = { showEditProfile = true },
                    onMyOrders = { showMyOrders = true },
                    onPaymentHistory = {
                        android.widget.Toast.makeText(context, "Payment History - Coming soon!", android.widget.Toast.LENGTH_SHORT).show()
                    },
                    onHelpSupport = {
                        android.widget.Toast.makeText(context, "Help & Support - Coming soon!", android.widget.Toast.LENGTH_SHORT).show()
                    },
                    onLogout = { activity.finish() }
                )
                }
            }
        }
    }
}

// ---- Keep DetailsScreen if you still use it ----
@Composable
fun DetailsScreen(itemId: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Details Screen for item: $itemId", style = MaterialTheme.typography.headlineMedium)
    }
}