package com.example.smartbite.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import android.widget.Toast
import com.example.smartbite.model.CartModel
import com.example.smartbite.viewmodel.CartViewModel
import com.example.smartbite.viewmodel.MenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    itemId: String,
    navController: NavController
) {
    val menuVM: MenuViewModel = viewModel()
    val cartVM: CartViewModel = viewModel()
    val context = LocalContext.current

    val menuItem by menuVM.menu.observeAsState()
    var quantity by remember { mutableStateOf(1) }

    LaunchedEffect(itemId) {
        menuVM.getMenuById(itemId)
    }

    if (menuItem == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val item = menuItem!!
    val totalPrice = item.price * quantity
    val isAvailable = item.isAvailable

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            // Image
            if (!item.imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (isAvailable) "Available" else "Unavailable",
                    color = if (isAvailable) Color(0xFF2E7D32) else Color.Red
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(item.desc)

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Rs ${item.price}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(20.dp))

            // Quantity
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { if (quantity > 1) quantity-- },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D4C41))
                ) { Text("-") }
                Spacer(Modifier.width(16.dp))
                Text(quantity.toString(), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.width(16.dp))
                Button(
                    onClick = { quantity++ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D4C41))
                ) { Text("+") }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Total: Rs $totalPrice",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
                    if (userId.isNullOrEmpty()) {
                        Toast.makeText(context, "Please login first!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    cartVM.addToCart(
                        CartModel(
                            id = item.id,
                            name = item.name,
                            price = item.price,
                            imageRes = item.imageRes,
                            quantity = quantity
                        )
                    )
                    Toast.makeText(context, "Added to cart!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                },
                enabled = isAvailable,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D4C41))
            ) {
                Text("Add to Cart")
            }
        }
    }
}