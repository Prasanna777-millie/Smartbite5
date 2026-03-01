package com.example.smartbite.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.smartbite.repo.CommonRepo

class CommonViewModel(private val repo: CommonRepo) : ViewModel() {

    fun uploadImage(context: Context, fileUri: Uri, callback: (String?) -> Unit) {
        repo.uploadImage(context, fileUri, callback)
    }

    fun getFileName(context: Context, fileUri: Uri): String? {
        return repo.getFileNameFromUri(context, fileUri)
    }
}