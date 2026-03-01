package com.example.smartbite.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartbite.R
import com.example.smartbite.view.LoginActivity
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartBiteSplash()
        }
    }
}

@Composable
fun SmartBiteSplash() {
    val context = LocalContext.current
    val activity = context as Activity

    // ☕ Café Palette
    val Espresso = Color(0xFF3E2723)
    val Caramel = Color(0xFFD7A86E)
    val Cream = Color(0xFFFFF3E0)

    LaunchedEffect(Unit) {
        delay(2500)
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
        activity.finish()
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Cream),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // 🖼 Logo (replace with your logo)
            Image(
                painter = painterResource(R.drawable.logoofcafe),
                contentDescription = "SmartBite Logo",
                modifier = Modifier.size(90.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "SMARTBITE",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Espresso,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Order smart. Eat happy.",
                fontSize = 13.sp,
                color = Espresso.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(50.dp))

            CircularProgressIndicator(
                color = Caramel,
                strokeWidth = 3.dp
            )
        }
    }
}