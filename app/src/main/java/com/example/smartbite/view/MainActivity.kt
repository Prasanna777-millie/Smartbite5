package com.example.smartbite.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.example.smartbite.view.LoginActivity

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Launch Login screen
        startActivity(Intent(this, LoginActivity::class.java))
        finish() // prevents coming back to MainActivity
    }
}