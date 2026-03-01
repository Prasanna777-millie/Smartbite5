package com.example.smartbite.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.smartbite.model.CartModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartViewModel : ViewModel() {

    val cartItems = mutableStateOf<List<CartModel>>(emptyList())

    private val db = FirebaseDatabase.getInstance().reference
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private var listener: ValueEventListener? = null

    fun listenCart() {
        if (userId.isEmpty()) return

        val cartRef = db.child("users").child(userId).child("cart")
        
        listener = cartRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<CartModel>()
                for (child in snapshot.children) {
                    val item = child.getValue(CartModel::class.java)
                    if (item != null) {
                        list.add(item)
                    }
                }
                cartItems.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("CartViewModel", "Listen failed: ${error.message}")
            }
        })
    }

    fun addToCart(item: CartModel) {
        if (userId.isEmpty()) {
            android.util.Log.e("CartViewModel", "User not logged in")
            return
        }

        android.util.Log.d("CartViewModel", "Adding to cart: ${item.name}, userId: $userId")

        val cartRef = db.child("users").child(userId).child("cart").child(item.id)
        
        cartRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val currentQty = snapshot.child("quantity").getValue(Int::class.java) ?: 1
                cartRef.child("quantity").setValue(currentQty + item.quantity)
                    .addOnSuccessListener {
                        android.util.Log.d("CartViewModel", "Updated quantity")
                    }
                    .addOnFailureListener { e ->
                        android.util.Log.e("CartViewModel", "Failed to update: ${e.message}")
                    }
            } else {
                cartRef.setValue(item)
                    .addOnSuccessListener {
                        android.util.Log.d("CartViewModel", "Added new item")
                    }
                    .addOnFailureListener { e ->
                        android.util.Log.e("CartViewModel", "Failed to add: ${e.message}")
                    }
            }
        }.addOnFailureListener { e ->
            android.util.Log.e("CartViewModel", "Failed to get: ${e.message}")
        }
    }

    fun increaseQty(itemId: String, currentQty: Int) {
        db.child("users").child(userId).child("cart").child(itemId)
            .child("quantity").setValue(currentQty + 1)
    }

    fun decreaseQty(itemId: String, currentQty: Int) {
        if (currentQty <= 1) return
        db.child("users").child(userId).child("cart").child(itemId)
            .child("quantity").setValue(currentQty - 1)
    }

    fun removeItem(itemId: String) {
        db.child("users").child(userId).child("cart").child(itemId).removeValue()
    }

    fun clearCart() {
        cartItems.value.forEach {
            removeItem(it.id)
        }
    }

    override fun onCleared() {
        if (listener != null) {
            db.child("users").child(userId).child("cart").removeEventListener(listener!!)
        }
        super.onCleared()
    }
}