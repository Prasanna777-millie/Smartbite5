package com.example.smartbite.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.smartbite.viewmodel.MenuViewModel

@Composable
fun AdminHomeScreen(navController: NavController) {

    val menuVM: MenuViewModel = viewModel()
    val menuList by menuVM.menuList.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        menuVM.getAllMenus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Admin Menu",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (menuList.isEmpty()) {
            Text("No menus found.")
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(menuList, key = { it.id }) { item ->
                    MenuItemCard(
                        title = item.name,
                        description = item.desc,
                        price = item.price,
                        imageUrl = item.imageUrl,
                        isAvailable = item.isAvailable,

                        mode = MenuCardMode.ADMIN,

                        onEdit = { navController.navigate("edit_menu/${item.id}") },
                        onDelete = {
                            menuVM.deleteMenu(item.id) { _, _ -> }
                        },
                        onToggleAvailability = {
                            // ✅ This calls the function in your ViewModel
                            menuVM.toggleMenuAvailability(item.id, item.isAvailable)
                        }
                    )
                }
            }
        }
    }
}
