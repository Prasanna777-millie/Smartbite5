package com.example.smartbite.repo

import android.content.Context
import android.net.Uri

interface CommonRepo {
    fun uploadImage(context: Context, fileUri: Uri, callback: (String?) -> Unit)
    fun getFileNameFromUri(context: Context, fileUri: Uri): String?
}