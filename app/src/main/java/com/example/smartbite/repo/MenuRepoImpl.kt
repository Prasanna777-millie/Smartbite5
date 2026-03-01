package com.example.smartbite.repo

import com.example.smartbite.model.MenuItemModel
import com.google.firebase.database.*

class MenuRepoImpl : MenuRepo {

    private val db: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = db.getReference("Menus")

    override fun addMenu(menu: MenuItemModel, callback: (Boolean, String) -> Unit) {
        val id = menu.id.ifEmpty {
            callback(false, "Menu ID is missing")
            return
        }

        ref.child(id)
            .setValue(menu)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) callback(true, "Menu added")
                else callback(false, task.exception?.message ?: "Failed to add menu")
            }
    }

    override fun getMenuById(menuId: String, callback: (Boolean, String, MenuItemModel?) -> Unit) {
        if (menuId.isEmpty()) {
            callback(false, "Menu ID is missing", null)
            return
        }

        ref.child(menuId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val menu = snapshot.getValue(MenuItemModel::class.java)
                        callback(true, "Menu fetched", menu)
                    } else {
                        callback(false, "Menu not found", null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, null)
                }
            })
    }

    override fun getAllMenus(callback: (Boolean, String, List<MenuItemModel>) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val menuList = mutableListOf<MenuItemModel>()
                for (child in snapshot.children) {
                    val menu = child.getValue(MenuItemModel::class.java)
                    if (menu != null) menuList.add(menu)
                }
                callback(true, "Menus fetched", menuList)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }
        })
    }

    override fun updateMenu(menu: MenuItemModel, callback: (Boolean, String) -> Unit) {
        if (menu.id.isEmpty()) {
            callback(false, "Menu ID is missing")
            return
        }

        ref.child(menu.id)
            .updateChildren(menu.toMap())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) callback(true, "Menu updated")
                else callback(false, task.exception?.message ?: "Update failed")
            }
    }

    override fun deleteMenu(menuId: String, callback: (Boolean, String) -> Unit) {
        if (menuId.isEmpty()) {
            callback(false, "Menu ID is missing")
            return
        }

        ref.child(menuId)
            .removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) callback(true, "Menu deleted")
                else callback(false, task.exception?.message ?: "Delete failed")
            }
    }

    override fun updateMenuAvailability(
        menuId: String,
        status: Boolean,
        callback: (Boolean, String) -> Unit
    ) {
        if (menuId.isEmpty()) {
            callback(false, "Menu ID is missing")
            return
        }

        ref.child(menuId)
            .updateChildren(mapOf("isAvailable" to status))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) callback(true, "Successfully updated")
                else callback(false, task.exception?.message ?: "Failed to update")
            }
    }
}