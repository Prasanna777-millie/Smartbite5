package com.example.smartbite.model

data class MenuItemModel(
    var id: String = "",
    var name: String = "",
    var desc: String = "",
    var price: Int = 0,
    var imageRes: Int = 0,
    var category: String = "",
    var isAvailable: Boolean = true,
    var imageUrl: String? = null
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "desc" to desc,
            "price" to price,
            "imageRes" to imageRes,
            "category" to category,
            "isAvailable" to isAvailable,
            "imageUrl" to imageUrl
        )
    }
}