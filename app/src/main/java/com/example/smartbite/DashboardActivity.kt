package com.example.smartbite

import android.content.Intent
import android.os.Bundle
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DashboardBody()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBody() {
    val context = LocalContext.current

    // Brown palette
    val Espresso = Color(0xFF3E2723)
    val Mocha = Color(0xFF6D4C41)
    val Caramel = Color(0xFFD7A86E)
    val Cream = Color(0xFFFFF3E0)

    data class NavItem(val label: String, val icon: Int)

    val listItems = listOf(
        NavItem(label = "Home", R.drawable.baseline_home_24),
        NavItem(label = "Setting", R.drawable.outline_settings_24),
        NavItem(label = "Person", R.drawable.outline_person_24)
    )
    var selectedIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "SmartBites",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        color = Espresso
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Caramel,
                    titleContentColor = Espresso
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Cream) {
                listItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = {
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = item.label,
                                tint = if (selectedIndex == index) Espresso else Mocha
                            )
                        },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { paddingValues ->
        when (selectedIndex) {
            0 -> HomeScreen(paddingValues)
            1 -> OrdersScreen(paddingValues)
            2 -> ProfileScreen(paddingValues)
        }
    }
}

@Composable
fun HomeScreen(paddingValues: PaddingValues) {
    val context = LocalContext.current

    val Espresso = Color(0xFF3E2723)
    val Mocha = Color(0xFF6D4C41)
    val Cream = Color(0xFFFFF3E0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
            .background(Cream)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Welcome 👋",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Espresso
        )

        Text(
            text = "Manage Your Café Orders!",
            fontSize = 16.sp,
            color = Mocha
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            DashboardCard(R.drawable.pasteries) {
                // Navigate to Create Order Page
            }
            DashboardCard(R.drawable.menu) {
                // Navigate to View Menu Page
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        DashboardCard(
            image = R.drawable.order,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            // Navigate to My Orders
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Special Offers",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Espresso
        )

        Spacer(modifier = Modifier.height(16.dp))

        UpcomingEventCard(
            image = R.drawable.offerr,
            title = "Buy 1 Get 1 Free Latte",
            place = "All Branches"
        )
        Spacer(modifier = Modifier.height(12.dp))
        UpcomingEventCard(
            image = R.drawable.discount,
            title = "Weekend Muffin Discount",
            place = "Baneshwor Branch"
        )
    }
}

@Composable
fun DashboardCard(
    image: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .width(120.dp)
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Image(
            painter = painterResource(image),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
fun UpcomingEventCard(
    image: Int,
    title: String,
    place: String
) {
    val Espresso = Color(0xFF3E2723)
    val Mocha = Color(0xFF6D4C41)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.size(68.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Image(
                    painter = painterResource(image),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(15.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Espresso
                )
                Text(
                    text = place,
                    fontSize = 13.sp,
                    color = Mocha
                )
            }
        }
    }
}

@Composable
fun OrdersScreen(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Text("Orders Screen")
    }
}

@Composable
fun ProfileScreen(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Text("Profile Screen")
    }
}

@Preview
@Composable
fun DashboardPreview() {
    DashboardBody()
}
