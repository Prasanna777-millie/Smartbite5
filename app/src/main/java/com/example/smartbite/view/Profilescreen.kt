package com.example.smartbite.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartbite.R
import com.example.smartbite.repo.ProfileRepoImpl
import com.example.smartbite.viewmodel.ProfileViewModel
import com.example.smartbite.viewmodel.ProfileViewModelFactory
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreenNew(
    isDarkMode: Boolean,
    onThemeChange: (Boolean) -> Unit,

    // actions (connect with nav)
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    onMyOrders: () -> Unit,
    onPaymentHistory: () -> Unit,
    onHelpSupport: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid ?: ""

    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(ProfileRepoImpl())
    )
    val profileData by profileViewModel.profile.observeAsState()

    var menuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            profileViewModel.getProfileById(userId)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 18.dp)
    ) {

        // ---------------- TOP BAR ----------------
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_keyboard_arrow_right_24),
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .size(26.dp)
                        .clickable { onBack() }
                )

                Text(
                    text = "Profile",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                menuExpanded = false
                                auth.signOut()
                                onLogout()
                            }
                        )
                    }
                }
            }
        }

        // ---------------- HEADER ----------------
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF4E342E), Color(0xFF6D4C41))
                        )
                    )
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // ✅ Simple local profile image (no AsyncImage)
                    Box(
                        modifier = Modifier
                            .size(86.dp)
                            .clip(CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "Profile",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        val fullName = profileData?.fullName?.trim().orEmpty()
                        val email = profileData?.email?.trim().orEmpty()

                        Text(
                            text = if (fullName.isNotEmpty()) fullName else "User",
                            color = Color.White,
                            fontSize = 20.sp
                        )

                        if (email.isNotEmpty()) {
                            Text(
                                text = email,
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 13.sp
                            )
                        }
                    }

                    FilledTonalButton(
                        onClick = { onEditProfile() },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.White.copy(alpha = 0.18f),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Text("Edit", fontSize = 13.sp)
                    }
                }
            }
        }

        // ---------------- MENU ----------------
        item { Spacer(Modifier.height(14.dp)) }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    SimpleMenuItem("My Orders") { onMyOrders() }
                    SimpleMenuItem("Payment History") { onPaymentHistory() }
                    SimpleMenuItem("Help & Support") { onHelpSupport() }
                }
            }
        }

        // ---------------- THEME SWITCH ----------------
        item { Spacer(Modifier.height(12.dp)) }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Dark Mode", color = MaterialTheme.colorScheme.onSurface)
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { onThemeChange(it) }
                    )
                }
            }
        }


    }
}

@Composable
private fun SimpleMenuItem(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface
        )

        // ✅ use your drawable arrow (no ChevronRight)
        Icon(
            painter = painterResource(id = R.drawable.baseline_keyboard_arrow_right_24),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface
        )
    }

    Divider(
        modifier = Modifier.padding(start = 14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    )
}