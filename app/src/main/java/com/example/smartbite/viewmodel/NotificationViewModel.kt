package com.example.smartbite.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.smartbite.model.NotificationModel
import com.google.firebase.database.*

class NotificationViewModel : ViewModel() {

    val notifications = mutableStateOf<List<NotificationModel>>(emptyList())
    private var listener: ValueEventListener? = null
    private var ref: DatabaseReference? = null

    fun listenNotifications(userId: String) {
        listener?.let { ref?.removeEventListener(it) }

        ref = FirebaseDatabase.getInstance()
            .getReference("users/$userId/notifications")

        listener = ref?.orderByChild("createdAt")
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children.mapNotNull { child ->
                        child.getValue(NotificationModel::class.java)?.copy(id = child.key ?: "")
                    }.sortedByDescending { it.createdAt }

                    notifications.value = list
                }

                override fun onCancelled(error: DatabaseError) {
                    // optional: log error.message
                }
            })
    }

    fun markAsRead(userId: String, notificationId: String) {
        FirebaseDatabase.getInstance()
            .getReference("users/$userId/notifications/$notificationId")
            .updateChildren(mapOf("isRead" to true))
    }

    override fun onCleared() {
        listener?.let { ref?.removeEventListener(it) }
        super.onCleared()
    }
}