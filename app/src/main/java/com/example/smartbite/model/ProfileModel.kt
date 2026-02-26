package com.example.smartbite.model

data class ProfileModel(
    var userId: String = "",
    var fullName: String = "",
    var email: String = "",
    var phone: String = "",
    var address: String = "",
    var imageUrl: String = ""
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "userId" to userId,
        "fullName" to fullName,
        "email" to email,
        "phone" to phone,
        "address" to address,
        "imageUrl" to imageUrl
    )
}