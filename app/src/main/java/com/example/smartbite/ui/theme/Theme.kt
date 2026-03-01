package com.example.smartbite.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Mocha,
    secondary = MediumBrown,
    tertiary = Caramel,
    background = Espresso,
    surface = DarkBrown,
    onPrimary = Cream,
    onSecondary = Cream,
    onBackground = Cream,
    onSurface = Cream
)

private val LightColorScheme = lightColorScheme(
    primary = DarkBrown,
    secondary = Mocha,
    tertiary = Caramel,
    background = LightCream,
    surface = White,
    onPrimary = White,
    onSecondary = White,
    onBackground = Espresso,
    onSurface = Espresso
)

@Composable
fun SmartBiteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}