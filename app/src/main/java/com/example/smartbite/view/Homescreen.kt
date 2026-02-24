package com.example.smartbite.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smartbite.R

private enum class Category { Coffee, Food }

private data class MenuItem(
    val id: String,
    val name: String,
    val desc: String,
    val price: Int,
    val imageRes: Int,
    val category: Category
)

@Composable
fun HomeScreen(navController: NavController) {

    val menuItems = listOf(
        MenuItem("coffee1","Caramel Hazelnut Iced Coffee","Sweet caramel + hazelnut blend.",220,R.drawable.caramelhazelnuticedcoffee,Category.Coffee),
        MenuItem("coffee2","Matcha Latte","Smooth matcha with milk.",240,R.drawable.matchalatte,Category.Coffee),
        MenuItem("coffee3","Cappuccino","Espresso with rich foam.",200,R.drawable.cappuccino,Category.Coffee),

        MenuItem("food1","Pastries","Fresh baked assorted pastries.",180,R.drawable.pasteries,Category.Food),
        MenuItem("food2","Chocolate Éclair","Creamy filling with glaze.",160,R.drawable.chocolateeclairs,Category.Food),
        MenuItem("food3","Cinnamon Roll","Soft roll with cinnamon icing.",170,R.drawable.cinamonroll,Category.Food)
    )

    var selectedCategory by remember { mutableStateOf(Category.Coffee) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredItems = menuItems
        .filter { it.category == selectedCategory }
        .filter { if (searchQuery.isBlank()) true else it.name.contains(searchQuery, ignoreCase = true) }

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
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selectedCategory == Category.Coffee) Color(0xFF4E342E) else Color.White)
                        .clickable { selectedCategory = Category.Coffee }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Coffee", color = if (selectedCategory == Category.Coffee) Color.White else Color.Black)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selectedCategory == Category.Food) Color(0xFF4E342E) else Color.White)
                        .clickable { selectedCategory = Category.Food }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Food", color = if (selectedCategory == Category.Food) Color.White else Color.Black)
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search...") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )
            Spacer(Modifier.height(18.dp))
        }

        item {
            Text(
                text = "Today Special",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(10.dp))
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                Card(
                    modifier = Modifier.weight(1f).height(130.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFB3B394))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Discount", fontWeight = FontWeight.Bold)
                        Image(
                            painter = painterResource(id = R.drawable.discount),
                            contentDescription = "Discount",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f).height(130.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFB3B394))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Offer", fontWeight = FontWeight.Bold)
                        Image(
                            painter = painterResource(id = R.drawable.offerr),
                            contentDescription = "Offer",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        val rows = filteredItems.chunked(2)
        items(rows) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                MenuCard(row[0], Modifier.weight(1f)) {
                    navController.navigate("details/${row[0].id}")
                }

                if (row.size == 2) {
                    MenuCard(row[1], Modifier.weight(1f)) {
                        navController.navigate("details/${row[1].id}")
                    }
                } else {
                    Spacer(Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun MenuCard(
    item: MenuItem,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.height(230.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = item.name,
                modifier = Modifier.fillMaxWidth().height(130.dp),
                contentScale = ContentScale.Crop
            )

            Column(Modifier.padding(10.dp)) {
                Text(item.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(item.desc, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)

                Spacer(Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Rs ${item.price}", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))

                    Box(
                        modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFF4E342E)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+", color = Color.White)
                    }
                }
            }
        }
    }
}