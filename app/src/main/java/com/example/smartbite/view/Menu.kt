package com.example.smartbite.data

import com.example.smartbite.R
import androidx.compose.runtime.mutableStateListOf

enum class Category { Coffee, Food }

data class MenuItem(
    val id: String,
    var name: String,
    var desc: String,
    var price: Int,
    val imageRes: Int,
    var category: Category,
    var isAvailable: Boolean = true
)

object MenuData {
    val menuList = mutableStateListOf(
        MenuItem("coffee1","Caramel Hazelnut Iced Coffee","Sweet caramel + hazelnut blend.",220,R.drawable.caramelhazelnuticedcoffee,Category.Coffee),
        MenuItem("coffee2","Matcha Latte","Smooth matcha with milk.",240,R.drawable.matchalatte,Category.Coffee),
        MenuItem("coffee3","Cappuccino","Espresso with rich foam.",200,R.drawable.cappuccino,Category.Coffee),
        MenuItem("food1","Pastries","Fresh baked assorted pastries.",180,R.drawable.pasteries,Category.Food),
        MenuItem("food2","Chocolate Éclair","Creamy filling with glaze.",160,R.drawable.chocolateeclairs,Category.Food),
        MenuItem("food3","Cinnamon Roll","Soft roll with cinnamon icing.",170,R.drawable.cinamonroll,Category.Food)
    )
}