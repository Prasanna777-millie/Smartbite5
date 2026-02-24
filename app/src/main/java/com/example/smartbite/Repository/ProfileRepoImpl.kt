package com.example.smartbite.repo

import com.example.smartbite.model.ProfileModel
import com.google.firebase.database.*

class ProfileRepoImpl : ProfileRepo {

    private val db: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = db.getReference("Users")

    override fun addProfile(model: ProfileModel, callback: (Boolean, String) -> Unit) {
        val id = model.userId.ifEmpty {
            callback(false, "UserId is missing")
            return
        }

        ref.child(id)
            .setValue(model)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) callback(true, "Profile saved")
                else callback(false, task.exception?.message ?: "Failed to save profile")
            }
    }

    override fun getProfileById(
        userId: String,
        callback: (Boolean, String, ProfileModel?) -> Unit
    ) {
        if (userId.isEmpty()) {
            callback(false, "UserId is missing", null)
            return
        }

        ref.child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val profile = snapshot.getValue(ProfileModel::class.java)
                        callback(true, "Profile fetched", profile)
                    } else {
                        callback(false, "Profile not found", null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, null)
                }
            })
    }

    override fun updateProfile(model: ProfileModel, callback: (Boolean, String) -> Unit) {
        if (model.userId.isEmpty()) {
            callback(false, "UserId is missing")
            return
        }

        ref.child(model.userId)
            .updateChildren(model.toMap())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) callback(true, "Profile updated")
                else callback(false, task.exception?.message ?: "Update failed")
            }
    }

    override fun deleteProfile(userId: String, callback: (Boolean, String) -> Unit) {
        if (userId.isEmpty()) {
            callback(false, "UserId is missing")
            return
        }

        ref.child(userId)
            .removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) callback(true, "Profile deleted")
                else callback(false, task.exception?.message ?: "Delete failed")
            }
    }
}