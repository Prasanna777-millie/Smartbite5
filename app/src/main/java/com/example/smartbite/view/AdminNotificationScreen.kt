package com.example.smartbite.view

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.smartbite.model.NotificationModel
import com.example.smartbite.viewmodel.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminNotificationScreen(navController: NavController) {
    val vm: NotificationViewModel = viewModel()
    val notifications by vm.notifications

    LaunchedEffect(Unit) {
        vm.listenNotifications("admin")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F6F3))
            .padding(16.dp)
    ) {
        Text(
            text = "Notifications",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No notifications yet")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(notifications) { notification ->
                    AdminNotificationCard(notification, vm)
                }
            }
        }
    }
}

@Composable
fun AdminNotificationCard(
    notification: NotificationModel,
    vm: NotificationViewModel
) {
    val db = com.google.firebase.database.FirebaseDatabase.getInstance().reference
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) Color.White else Color(0xFFFFF3E0)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (notification.isRead) Color.Gray else Color(0xFFFF6F00))
                )

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = notification.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = notification.message,
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = formatTimeForAdmin(notification.createdAt),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            
            if (notification.type == "order" && notification.orderId != null && notification.userId != null) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Status: ${notification.orderStatus ?: "Pending"}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6D4C41)
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            updateOrderStatus(db, notification, "Preparing")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Preparing", fontSize = 12.sp)
                    }
                    Button(
                        onClick = {
                            updateOrderStatus(db, notification, "Ready")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Ready", fontSize = 12.sp)
                    }
                    Button(
                        onClick = {
                            updateOrderStatus(db, notification, "Completed")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Completed", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

fun updateOrderStatus(db: com.google.firebase.database.DatabaseReference, notification: NotificationModel, status: String) {
    val orderId = notification.orderId ?: return
    val userId = notification.userId ?: return
    
    // Update admin notification status
    db.child("users/admin/notifications/${notification.id}")
        .updateChildren(mapOf("orderStatus" to status))
    
    // Send notification to user
    val userNotificationId = db.child("users/$userId/notifications").push().key ?: return
    val userNotification = mapOf(
        "id" to userNotificationId,
        "type" to "order_update",
        "title" to "Order Update",
        "message" to "Your order is now $status",
        "orderId" to orderId,
        "orderStatus" to status,
        "isRead" to false,
        "createdAt" to System.currentTimeMillis()
    )
    db.child("users/$userId/notifications/$userNotificationId").setValue(userNotification)
}

fun formatTimeForAdmin(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}