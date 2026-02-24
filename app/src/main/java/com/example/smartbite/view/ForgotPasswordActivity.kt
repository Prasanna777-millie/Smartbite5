package com.example.smartbite

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartbite.viewmodel.UserViewModel
import com.example.smartbite.repository.UserRepoImpl

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ForgotPasswordBody()
        }
    }
}

@Composable
fun ForgotPasswordBody() {

    val context = LocalContext.current
    val userViewModel = remember { UserViewModel(UserRepoImpl()) }

    var email by remember { mutableStateOf("") }

    // ☕ SmartBite Brown Palette
    val Espresso = Color(0xFF3E2723)
    val Mocha = Color(0xFF6D4C41)
    val Caramel = Color(0xFFD7A86E)
    val Cream = Color(0xFFFFF3E0)
    val Latte = Color(0xFFFFE0B2)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Cream, Latte)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Forgot Password",
                modifier = Modifier.height(180.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Cream)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Forgot Password",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Espresso
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "We’ll send a reset link to your email",
                        fontSize = 13.sp,
                        color = Mocha
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Enter your email") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Cream,
                            unfocusedContainerColor = Cream,
                            focusedIndicatorColor = Caramel,
                            unfocusedIndicatorColor = Mocha,
                            focusedTextColor = Espresso,
                            unfocusedTextColor = Espresso
                        )
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {
                            if (email.isBlank()) {
                                Toast.makeText(
                                    context,
                                    "Enter your email",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                userViewModel.forgetPassword(email.trim()) { success, message ->
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    if (success) {
                                        context.startActivity(
                                            Intent(context, LoginActivity::class.java)
                                        )
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Caramel)
                    ) {
                        Text(
                            text = "Send Reset Link",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = {
                            context.startActivity(
                                Intent(context, LoginActivity::class.java)
                            )
                        }
                    ) {
                        Text(
                            text = "Back to Login",
                            color = Mocha
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ForgetPasswordPreview() {
    ForgotPasswordBody()
}
