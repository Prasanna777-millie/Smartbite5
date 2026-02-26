package com.example.smartbite.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smartbite.model.ProfileModel
import com.example.smartbite.repo.ProfileRepo

class ProfileViewModel(private val repo: ProfileRepo) : ViewModel() {

    private val _profile = MutableLiveData<ProfileModel?>()
    val profile: MutableLiveData<ProfileModel?> get() = _profile

    fun addProfile(model: ProfileModel, callback: (Boolean, String) -> Unit) {
        repo.addProfile(model, callback)
    }

    fun updateProfile(model: ProfileModel, callback: (Boolean, String) -> Unit) {
        repo.updateProfile(model, callback)
    }

    fun deleteProfile(userId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteProfile(userId, callback)
    }

    fun getProfileById(userId: String) {
        repo.getProfileById(userId) { success, _, data ->
            if (success) _profile.postValue(data)
        }
    }
}