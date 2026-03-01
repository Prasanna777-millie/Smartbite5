package com.example.smartbite.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartbite.model.NotificationModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MyOrdersScreen(onBack: () -> Unit) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var orders by remember { mutableStateOf<List<NotificationModel>>(emptyList()) }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            FirebaseDatabase.getInstance()
                .getReference("users/$userId/notifications")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val list = snapshot.children.mapNotNull {
                            it.getValue(NotificationModel::class.java)?.copy(id = it.key ?: "")
                        }.filter { it.type == "order_update" || it.type == "order" }
                        .sortedByDescending { it.createdAt }
                        orders = list
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F6F3))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                androidx.compose.material3.Icon(
                    painter = androidx.compose.ui.res.painterResource(com.example.smartbite.R.drawable.baseline_keyboard_arrow_right_24),
                    contentDescription = "Back"
                )
            }
            Text(
                text = "My Orders",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4E342E)
            )
        }
        Spacer(Modifier.height(16.dp))

        if (orders.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No orders yet", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(orders) { order ->
                    OrderCard(order)
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: NotificationModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Order #${order.orderId?.takeLast(6) ?: "N/A"}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF4E342E)
                )
                
                val statusColor = when (order.orderStatus) {
                    "Preparing" -> Color(0xFFFF9800)
                    "Ready" -> Color(0xFF4CAF50)
                    "Completed" -> Color(0xFF2196F3)
                    else -> Color.Gray
                }
                
                Text(
                    text = order.orderStatus ?: "Pending",
                    color = statusColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
            
            Spacer(Modifier.height(8.dp))
            
            Text(
                text = order.message,
                fontSize = 14.sp,
                color = Color.DarkGray
            )
            
            Spacer(Modifier.height(8.dp))
            
            Text(
                text = formatTime(order.createdAt),
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
