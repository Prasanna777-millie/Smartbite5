package com.example.smartbite.repository

import com.example.smartbite.model.UserModel

interface UserRepo {
    //    {
//        "success":true,
//        "message":"Verification link sent to your email address",
//        "userId":"sasas"
//    }
    fun login(
        email: String, password: String,
        callback: (Boolean, String) -> Unit
    )

    fun register(
        email: String, password: String,
        callback: (Boolean, String, String) -> Unit
    )

    fun addUserToDatabase(
        userId: String, model: UserModel,
        callback: (Boolean, String) -> Unit
    )

    fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    )

    fun deleteAccount(userId: String, callback: (Boolean, String) -> Unit)

    fun editProfile(
        userId: String, model: UserModel,
        callback: (Boolean, String) -> Unit
    )

    fun getUserById(
        userId: String,
        callback: (Boolean, String, UserModel?) -> Unit
    )

    fun getAllUser(callback: (Boolean, String, List<UserModel>?) -> Unit)
}