package com.example.smartbite.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartbite.viewmodel.CartViewModel

@Composable
fun CartScreen(
    vm: CartViewModel = viewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val cartItems by vm.cartItems
    val accent = Color(0xFF4E342E)

    LaunchedEffect(Unit) {
        vm.listenCart()
    }

    val subtotal = cartItems.sumOf { it.price * it.quantity }
    val vat = (subtotal * 0.13).toInt()
    val total = subtotal + vat

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F6F3))
            .padding(16.dp)
    ) {

        Text("Your Cart", fontSize = 22.sp)
        Spacer(Modifier.height(12.dp))

        if (cartItems.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Cart is empty")
            }
            return@Column
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(cartItems) { item ->
                Card {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(item.name)
                            Text("Rs ${item.price}")
                        }

                        Row {
                            TextButton(onClick = {
                                vm.decreaseQty(item.id, item.quantity)
                            }) { Text("-") }

                            Text("${item.quantity}")

                            TextButton(onClick = {
                                vm.increaseQty(item.id, item.quantity)
                            }) { Text("+") }
                        }

                        TextButton(onClick = {
                            vm.removeItem(item.id)
                        }) {
                            Text("Remove", color = Color.Red)
                        }
                    }
                }
            }
        }

        Divider()

        Text("Subtotal: Rs $subtotal")
        Text("VAT (13%): Rs $vat")
        Text("Total: Rs $total")

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                // Send notification to admin
                val db = com.google.firebase.database.FirebaseDatabase.getInstance().reference
                val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return@Button
                val orderId = System.currentTimeMillis().toString()
                val notificationId = db.child("users").child("admin").child("notifications").push().key ?: return@Button
                
                val notification = mapOf(
                    "id" to notificationId,
                    "type" to "order",
                    "title" to "New Order Placed",
                    "message" to "Order total: Rs $total from user",
                    "orderId" to orderId,
                    "userId" to userId,
                    "orderStatus" to "Pending",
                    "isRead" to false,
                    "createdAt" to System.currentTimeMillis()
                )
                
                db.child("users").child("admin").child("notifications").child(notificationId)
                    .setValue(notification)
                    .addOnSuccessListener {
                        // Save order for user too
                        val userNotificationId = db.child("users/$userId/notifications").push().key ?: return@addOnSuccessListener
                        val userNotification = mapOf(
                            "id" to userNotificationId,
                            "type" to "order",
                            "title" to "Order Placed",
                            "message" to "Your order of Rs $total has been placed",
                            "orderId" to orderId,
                            "orderStatus" to "Pending",
                            "isRead" to false,
                            "createdAt" to System.currentTimeMillis()
                        )
                        db.child("users/$userId/notifications/$userNotificationId").setValue(userNotification)
                        
                        vm.clearCart()
                        android.widget.Toast.makeText(
                            context,
                            "Order placed successfully!",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = accent)
        ) {
            Text("Place Order")
        }
    }
}