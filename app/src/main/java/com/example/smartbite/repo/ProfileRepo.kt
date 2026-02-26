package com.example.smartbite.repo

import com.example.smartbite.model.ProfileModel

interface ProfileRepo {

    fun addProfile(model: ProfileModel, callback: (Boolean, String) -> Unit)

    fun getProfileById(
        userId: String,
        callback: (Boolean, String, ProfileModel?) -> Unit
    )

    fun updateProfile(model: ProfileModel, callback: (Boolean, String) -> Unit)

    fun deleteProfile(userId: String, callback: (Boolean, String) -> Unit)
}