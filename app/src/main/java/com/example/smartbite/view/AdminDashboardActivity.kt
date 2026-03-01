package com.example.smartbite.view


import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartbite.R
import com.example.smartbite.ui.theme.SmartBiteTheme

class AdminDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            var isDarkMode by remember { mutableStateOf(false) }

            SmartBiteTheme(darkTheme = isDarkMode) {
                AdminDashboardScreen(
                    isDarkMode = isDarkMode,
                    onThemeChange = { newMode -> isDarkMode = newMode }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    isDarkMode: Boolean = false,
    onThemeChange: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as Activity
    val navController = rememberNavController()
    var showMenu by remember { mutableStateOf(false) }

    data class NavItem(val label: String, val icon: Int)

    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {

        composable("dashboard") {

            var selectedIndex by remember { mutableStateOf(0) }

            val navItems = listOf(
                NavItem("Admin", R.drawable.baseline_home_24),
                NavItem("Notifications", R.drawable.baseline_notifications_24),

            )

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Admin Dashboard") },
                        actions = {
                            Box {
                                IconButton(onClick = { showMenu = true }) {
                                    Icon(
                                        painter = painterResource(R.drawable.baseline_more_vert_24),
                                        contentDescription = "Menu"
                                    )
                                }
                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Logout") },
                                        onClick = {
                                            showMenu = false
                                            com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                                            val intent = android.content.Intent(context, LoginActivity::class.java)
                                            intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            context.startActivity(intent)
                                            activity.finish()
                                        }
                                    )
                                }
                            }
                        }
                    )
                },

                // ✅ Floating Add Button
                floatingActionButton = {
                    if (selectedIndex == 0) {
                        FloatingActionButton(
                            onClick = { navController.navigate("add_menu") }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Menu Item")
                        }
                    }
                },

                bottomBar = {
                    NavigationBar {
                        navItems.forEachIndexed { index, item ->
                            NavigationBarItem(
                                selected = selectedIndex == index,
                                onClick = { selectedIndex = index },
                                icon = {
                                    Icon(
                                        painterResource(item.icon),
                                        contentDescription = item.label
                                    )
                                },
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
                    when (selectedIndex) {
                        0 -> AdminHomeScreen(navController)
                        1 -> AdminNotificationScreen(navController)
                    }
                }
            }
        }

        // ✅ This calls your EXISTING screen
        composable("add_menu") {
            AddMenuItemScreen(
                onItemAdded = {
                    navController.popBackStack() // go back after saving
                }
            )
        }

        composable("edit_menu/{menuId}") { backStackEntry ->
            val menuId = backStackEntry.arguments?.getString("menuId") ?: ""
            val menuVM: com.example.smartbite.viewmodel.MenuViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            val menuList by menuVM.menuList.observeAsState(emptyList())
            val menuItem = menuList.find { it.id == menuId }

            LaunchedEffect(Unit) {
                menuVM.getAllMenus()
            }

            if (menuItem != null) {
                AddMenuItemScreen(
                    menuItem = menuItem,
                    onItemAdded = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}