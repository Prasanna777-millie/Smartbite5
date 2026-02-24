package com.example.smartbite.model

data class MenuItemModel(
    val id: String,
    val name: String,
    val desc: String,
    val price: Int,
    val imageRes: Int,
    val category: Category,
    val isAvailable: Boolean = true
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "desc" to desc,
            "price" to price,
            "imageRes" to imageRes,
            "category" to category.name,  // Store enum as String
            "isAvailable" to isAvailable
        )
    }
}