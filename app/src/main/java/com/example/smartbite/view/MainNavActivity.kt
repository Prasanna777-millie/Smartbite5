package com.example.smartbite.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.smartbite.ui.theme.SmartBiteTheme
import com.google.firebase.auth.FirebaseAuth

class MainNavActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {

            var isDarkMode by remember { mutableStateOf(false) }

            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            SmartBiteTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNav(
                        isDarkMode = isDarkMode,
                        onThemeChange = { newMode -> isDarkMode = newMode },
                        currentUserId = currentUserId
                    )
                }
            }
        }
    }
}