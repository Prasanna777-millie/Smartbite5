package com.example.smartbite.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartbite.R
import com.example.smartbite.repository.UserRepoImpl
import com.example.smartbite.view.UserDashboardActivity
import com.example.smartbite.viewmodel.UserViewModel

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginBody()
        }
    }
}

@Composable
fun LoginBody() {
    val context = LocalContext.current
    val userViewModel = remember { UserViewModel(UserRepoImpl()) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var visibility by remember { mutableStateOf(false) }

    // ☕ SmartBite Brown Palette
    val Espresso = Color(0xFF3E2723)
    val Mocha = Color(0xFF6D4C41)
    val Caramel = Color(0xFFD7A86E)
    val Cream = Color(0xFFFFF3E0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(55.dp))

        Text(
            text = "SMARTBITE",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Espresso
        )
        Spacer(modifier = Modifier.height(20.dp))

        Image(
            painter = painterResource(R.drawable.logoofcafe),
            contentDescription = null,
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "From the first sip to the last drop, brewed to energize your day.",
            fontSize = 16.sp,
            color = Espresso,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 25.dp)
        )
        Spacer(modifier = Modifier.height(30.dp))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Enter Your Email", color = Mocha) },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .testTag("email"),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Cream,
                unfocusedContainerColor = Cream,
                focusedIndicatorColor = Mocha,
                unfocusedIndicatorColor = Mocha,
                focusedTextColor = Espresso,
                unfocusedTextColor = Espresso
            )
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("*******", color = Mocha) },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .testTag("password"),
            visualTransformation = if (visibility) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { visibility = !visibility }) {
                    Icon(
                        painter = if (visibility)
                            painterResource(R.drawable.baseline_visibility_off_24)
                        else
                            painterResource(R.drawable.baseline_visibility_24),
                        contentDescription = null,
                        tint = Mocha
                    )
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Cream,
                unfocusedContainerColor = Cream,
                focusedIndicatorColor = Mocha,
                unfocusedIndicatorColor = Mocha,
                focusedTextColor = Espresso,
                unfocusedTextColor = Espresso
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            "Forget Password?",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .clickable {
                    val intent = Intent(context, ForgotPasswordActivity::class.java)
                    context.startActivity(intent)
                },
            textAlign = TextAlign.End,
            color = Mocha,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(25.dp))

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Fields can't be empty", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                userViewModel.login(email.trim(), password.trim()) { success, msg ->
                    if (success) {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        
                        // Check if admin
                        val isAdmin = email.trim().equals("adminsmartbite@gmail.com", ignoreCase = true)
                        
                        val intent = if (isAdmin) {
                            Intent(context, AdminDashboardActivity::class.java)
                        } else {
                            Intent(context, UserDashboardActivity::class.java)
                        }
                        
                        context.startActivity(intent)
                        (context as? ComponentActivity)?.finish()
                    } else {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier
                .width(300.dp)
                .height(54.dp)
                .testTag("login"),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Caramel)
        ) {
            Text(
                text = "Log In",
                fontSize = 16.sp,
                color = Cream
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Don’t have an account? Create one",
            fontSize = 14.sp,
            color = Mocha,
            modifier = Modifier.clickable {
                val intent = Intent(context, RegistrationActivity::class.java)
                context.startActivity(intent)
            }
        )
    }
}

@Preview
@Composable
fun LoginPreview() {
    LoginBody()
}
