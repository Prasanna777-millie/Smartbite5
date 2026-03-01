package com.example.smartbite.repo

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import java.io.InputStream
import java.util.concurrent.Executors

class CommonRepoImpl : CommonRepo {

    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dbsec0xv8",
            "api_key" to "275289428763246",
            "api_secret" to "ycZVuRg_keIlZL_2HU0brwg46uQ"
        )
    )

    override fun uploadImage(
        context: Context,
        fileUri: Uri,
        callback: (String?) -> Unit
    ) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val inputStream: InputStream = context.contentResolver.openInputStream(fileUri)
                    ?: throw Exception("Unable to open input stream")

                var fileName = getFileNameFromUri(context, fileUri)
                fileName = fileName?.substringBeforeLast(".") ?: "uploaded_image_${System.currentTimeMillis()}"

                val response = cloudinary.uploader().upload(
                    inputStream,
                    ObjectUtils.asMap(
                        "public_id", fileName,
                        "resource_type", "image" // ✅ IMPORTANT: image upload
                    )
                )

                // ✅ secure https url
                val imageUrl = response["secure_url"] as String?

                Handler(Looper.getMainLooper()).post {
                    callback(imageUrl)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                android.util.Log.e("CloudinaryUpload", "Upload failed: ${e.message}")
                Handler(Looper.getMainLooper()).post {
                    callback(null)
                }
            }
        }
    }

    override fun getFileNameFromUri(context: Context, fileUri: Uri): String? {
        var fileName: String? = null
        val cursor: Cursor? = context.contentResolver.query(fileUri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) fileName = it.getString(nameIndex)
            }
        }
        return fileName
    }
}