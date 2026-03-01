package com.example.smartbite.view

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.smartbite.R
import com.example.smartbite.model.MenuItemModel
import com.example.smartbite.repo.CommonRepoImpl
import com.example.smartbite.repo.MenuRepoImpl
import com.example.smartbite.viewmodel.MenuViewModel
import com.example.smartbite.viewmodel.CommonViewModel


@Composable
fun AddMenuItemScreen(
    menuItem: MenuItemModel? = null,
    onItemAdded: () -> Unit
) {

    val context = LocalContext.current

    // Cloudinary uploader VM
    val commonViewModel = remember { CommonViewModel(CommonRepoImpl()) }

    // Firestore menu VM
    val menuViewModel = remember { MenuViewModel(MenuRepoImpl()) }

    var name by remember { mutableStateOf(menuItem?.name ?: "") }
    var desc by remember { mutableStateOf(menuItem?.desc ?: "") }
    var price by remember { mutableStateOf(menuItem?.price?.toString() ?: "") }
    var selectedCategory by remember { mutableStateOf(menuItem?.category ?: "Coffee") } // ✅ String category

    // Image picker
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // upload states
    var isUploading by remember { mutableStateOf(false) }
    var uploadedImageUrl by remember { mutableStateOf<String?>(menuItem?.imageUrl) }

    val imageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri = it
            uploadedImageUrl = null
            Toast.makeText(context, "Image selected", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            if (menuItem != null) "Edit Menu Item" else "Add Menu Item",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = desc,
            onValueChange = { desc = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price") },
            modifier = Modifier.fillMaxWidth()
        )

        // Category buttons (String)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Coffee", "Food").forEach { cat ->
                Button(
                    onClick = { selectedCategory = cat },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedCategory == cat) Color(0xFF4E342E) else Color.LightGray,
                        contentColor = Color.White
                    )
                ) { Text(cat) }
            }
        }

        // Image picker box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                .clickable(enabled = !isUploading) { imageLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            when {
                imageUri != null -> {
                    coil3.compose.AsyncImage(
                        model = imageUri,
                        contentDescription = "Selected image",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                uploadedImageUrl != null -> {
                    coil3.compose.AsyncImage(
                        model = uploadedImageUrl,
                        contentDescription = "Uploaded image",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    Text(
                        if (isUploading) "Uploading image..." else "Tap to select image"
                    )
                }
            }
        }

        // Upload image button (optional)
        Button(
            enabled = imageUri != null && !isUploading && uploadedImageUrl == null,
            onClick = {
                val uri = imageUri ?: return@Button
                isUploading = true

                // NOTE: If your CommonViewModel function is uploadFile(), rename here.
                commonViewModel.uploadImage(context, uri) { url ->
                    isUploading = false
                    if (url != null) {
                        uploadedImageUrl = url
                        Toast.makeText(context, "Image uploaded ✅", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Upload Image")
        }

        // Add Item -> Save to Firestore
        Button(
            enabled = !isUploading,
            onClick = {
                if (name.isBlank() || desc.isBlank() || price.isBlank()) {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // If image selected but not uploaded, upload then save
                if (imageUri != null && uploadedImageUrl == null) {
                    isUploading = true
                    val uri = imageUri!!

                    commonViewModel.uploadImage(context, uri) { url ->
                        isUploading = false
                        if (url == null) {
                            Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
                            return@uploadImage
                        }

                        uploadedImageUrl = url

                        val newItem = MenuItemModel(
                            id = menuItem?.id ?: System.currentTimeMillis().toString(),
                            name = name,
                            desc = desc,
                            price = price.toIntOrNull() ?: 0,
                            imageRes = R.drawable.cappuccino,
                            category = selectedCategory,
                            imageUrl = url
                        )

                val saveAction = if (menuItem != null) menuViewModel::updateMenu else menuViewModel::addMenu
                saveAction(newItem) { ok, msg ->
                    if (ok) {
                        Toast.makeText(context, "Saved ✅", Toast.LENGTH_SHORT).show()

                        // Send notification to all users
                        val db = com.google.firebase.database.FirebaseDatabase.getInstance().reference
                        db.child("users").get().addOnSuccessListener { snapshot ->
                            snapshot.children.forEach { userSnapshot ->
                                val userId = userSnapshot.key ?: return@forEach
                                if (userId != "admin") {
                                    val notificationId = db.child("users").child(userId).child("notifications").push().key ?: return@forEach
                                    val notification = mapOf(
                                        "id" to notificationId,
                                        "type" to "menu",
                                        "title" to "New ${selectedCategory} Added!",
                                        "message" to "Check out our new item: ${name}",
                                        "menuId" to newItem.id,
                                        "isRead" to false,
                                        "createdAt" to System.currentTimeMillis()
                                    )
                                    db.child("users").child(userId).child("notifications").child(notificationId).setValue(notification)
                                }
                            }
                        }

                        // Reset
                        name = ""
                        desc = ""
                        price = ""
                        selectedCategory = "Coffee"
                        imageUri = null
                        uploadedImageUrl = null

                        onItemAdded()
                    } else {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                }
                    }
                    return@Button
                }

                // No image OR already uploaded -> save directly
                val newItem = MenuItemModel(
                    id = menuItem?.id ?: System.currentTimeMillis().toString(),
                    name = name,
                    desc = desc,
                    price = price.toIntOrNull() ?: 0,
                    imageRes = R.drawable.cappuccino,
                    category = selectedCategory,
                    imageUrl = uploadedImageUrl
                )

                val saveAction = if (menuItem != null) menuViewModel::updateMenu else menuViewModel::addMenu
                saveAction(newItem) { ok, msg ->
                    if (ok) {
                        Toast.makeText(context, "Saved ✅", Toast.LENGTH_SHORT).show()

                        // Send notification to all users
                        val db = com.google.firebase.database.FirebaseDatabase.getInstance().reference
                        db.child("users").get().addOnSuccessListener { snapshot ->
                            snapshot.children.forEach { userSnapshot ->
                                val userId = userSnapshot.key ?: return@forEach
                                if (userId != "admin") {
                                    val notificationId = db.child("users").child(userId).child("notifications").push().key ?: return@forEach
                                    val notification = mapOf(
                                        "id" to notificationId,
                                        "type" to "menu",
                                        "title" to "New ${selectedCategory} Added!",
                                        "message" to "Check out our new item: ${name}",
                                        "menuId" to newItem.id,
                                        "isRead" to false,
                                        "createdAt" to System.currentTimeMillis()
                                    )
                                    db.child("users").child(userId).child("notifications").child(notificationId).setValue(notification)
                                }
                            }
                        }

                        // Reset
                        name = ""
                        desc = ""
                        price = ""
                        selectedCategory = "Coffee"
                        imageUri = null
                        uploadedImageUrl = null

                        onItemAdded()
                    } else {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isUploading) "Please wait..." else if (menuItem != null) "Update Item" else "Add Item")
        }
    }
}