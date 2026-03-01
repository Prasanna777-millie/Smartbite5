package com.example.smartbite.view

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.smartbite.model.NotificationModel
import com.example.smartbite.viewmodel.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationScreen(
    navController: NavController,
    currentUserId: String,
    vm: NotificationViewModel = viewModel()
) {
    val notifications by vm.notifications
    val accent = Color(0xFF4E342E)

    LaunchedEffect(currentUserId) {
        vm.listenNotifications(currentUserId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F6F3))
            .padding(16.dp)
    ) {
        Text("Notifications", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        if (notifications.isEmpty()) {
            Text("No notifications", color = Color.Gray)
            return@Column
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(notifications, key = { it.id }) { notif ->
                NotificationCard(
                    notif = notif,
                    accent = accent,
                    onClick = {
                        if (!notif.isRead) {
                            vm.markAsRead(currentUserId, notif.id)
                        }

                        // Optional navigation:
                        // if (notif.type == "ORDER") navController.navigate("orderDetails/${notif.orderId}")
                        // if (notif.type == "MENU") navController.navigate("details/${notif.menuId}")
                    }
                )
            }
        }
    }
}

@Composable
private fun NotificationCard(
    notif: NotificationModel,
    accent: Color,
    onClick: () -> Unit
) {
    val bg = if (notif.isRead) Color.White else accent.copy(alpha = 0.07f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (notif.isRead) Color.LightGray else accent)
            )

            Spacer(Modifier.width(10.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = notif.title,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = notif.message,
                    fontSize = 13.sp,
                    color = Color(0xFF333333)
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = formatNotificationTime(notif.createdAt),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

private fun formatNotificationTime(timeMillis: Long): String {
    if (timeMillis == 0L) return ""
    val sdf = SimpleDateFormat("MMM d, hh:mm a", Locale.getDefault())
    return sdf.format(java.util.Date(timeMillis))
}