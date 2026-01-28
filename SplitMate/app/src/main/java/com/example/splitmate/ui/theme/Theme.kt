package com.example.splitmate.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF418643),
    secondary = Color(0xFF408C42),
    tertiary = Color(0xFF018786),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF3E733F),
    secondary = Color(0xFF37803B),
    tertiary = Color(0xFF3C813F),
    background = Color(0xFF121212),
    surface = Color(0xFF121212),
)

@Composable
fun SplitMateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}