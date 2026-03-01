package com.example.smartbite.model

data class NotificationModel(
    val id: String = "",
    val type: String = "",
    val title: String = "",
    val message: String = "",
    val orderId: String? = null,
    val menuId: String? = null,
    val userId: String? = null,
    val orderStatus: String? = null,
    val isRead: Boolean = false,
    val markAsRead: Boolean=false,
    val createdAt: Long = 0L
)
