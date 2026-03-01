package com.example.smartbite.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.smartbite.viewmodel.MenuViewModel


private enum class Category { All, Coffee, Food }

@Composable
fun Homescreen(navController: NavController) {

    // ✅ Correct ViewModel call
    val menuVM: MenuViewModel = viewModel()

    val menuList by menuVM.menuList.observeAsState(emptyList())

    var selectedCategory by remember { mutableStateOf(Category.All) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        menuVM.getAllMenus() // 🔥 Load from Firebase
    }

    // ✅ Backend filter
    val filteredItems = menuList
        .filter {
            if (selectedCategory == Category.All) return@filter true
            val c = it.category.trim().lowercase()
            when (selectedCategory) {
                Category.Coffee -> c == "coffee"
                Category.Food -> c == "food"
                else -> true
            }
        }
        .filter {
            searchQuery.isBlank() ||
                    it.name.contains(searchQuery, ignoreCase = true)
        }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F6F3)),
        contentPadding = PaddingValues(16.dp)
    ) {

        item {
            Text(
                text = "Grab your coffee",
                fontSize = 22.sp,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(12.dp))
        }

        // ✅ Category Buttons
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                CategoryChip(
                    text = "All",
                    selected = selectedCategory == Category.All
                ) { selectedCategory = Category.All }

                CategoryChip(
                    text = "Coffee",
                    selected = selectedCategory == Category.Coffee
                ) { selectedCategory = Category.Coffee }

                CategoryChip(
                    text = "Food",
                    selected = selectedCategory == Category.Food
                ) { selectedCategory = Category.Food }
            }

            Spacer(Modifier.height(12.dp))
        }

        // ✅ Search
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search...") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(Modifier.height(16.dp))
        }

        if (filteredItems.isEmpty()) {
            item {
                Text("No menus found.")
            }
        } else {

            items(filteredItems, key = { it.id }) { item ->
                android.util.Log.d("Homescreen", "Item: ${item.name}, imageUrl: ${item.imageUrl}")

                MenuItemCard(
                    title = item.name,
                    description = item.desc,
                    price = item.price,
                    imageUrl = item.imageUrl,
                    isAvailable = item.isAvailable,
                    mode = MenuCardMode.USER,
                    onClick = {
                        navController.navigate("details/${item.id}")
                    }
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                if (selected) Color(0xFF4E342E) else Color.White,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else Color.Black
        )
    }
}