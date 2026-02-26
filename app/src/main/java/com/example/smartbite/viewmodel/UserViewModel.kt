package com.example.smartbite.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.example.smartbite.model.UserModel
import com.example.smartbite.repository.UserRepo

class UserViewModel(val repo: UserRepo) : ViewModel(){
    fun login(
        email: String, password: String,
        callback: (Boolean, String) -> Unit
    ){
        repo.login(email,password,callback)
    }

    fun register(
        email: String, password: String,
        callback: (Boolean, String, String) -> Unit
    ){
        repo.register(email,password,callback)
    }

    fun addUserToDatabase(
        userId: String, model: UserModel,
        callback: (Boolean, String) -> Unit
    ){
        repo.addUserToDatabase(userId,model,callback)
    }

    fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ){
        repo.forgetPassword(email,callback)
    }

    fun deleteAccount(userId: String, callback: (Boolean, String) -> Unit){
        repo.deleteAccount(userId,callback)
    }

    fun editProfile(
        userId: String, model: UserModel,
        callback: (Boolean, String) -> Unit
    ){
        repo.editProfile(userId,model,callback)
    }

    private val _users = MutableLiveData<UserModel?>()
    val users : MutableLiveData<UserModel?>
        get() = _users

    private val _allUsers = MutableLiveData<List<UserModel>?>()
    val allUsers : MutableLiveData<List<UserModel>?>
        get() = _allUsers

    fun getUserById(
        userId: String
    ){
        repo.getUserById(userId){
                success,msg,data->
            if(success){
                _users.postValue(data)
            }
        }
    }

    fun getAllUser(){
        repo.getAllUser {
                success,msg,data->
            if(success){
                _allUsers.postValue(data)
            }
        }
    }
}