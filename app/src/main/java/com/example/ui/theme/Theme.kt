package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DasaniColorScheme = darkColorScheme(
    primary = DasaniPrimary,
    onPrimary = DasaniOnPrimary,
    primaryContainer = DasaniPrimaryContainer,
    onPrimaryContainer = DasaniPrimary,
    secondary = DasaniSecondary,
    onSecondary = DasaniOnSecondary,
    secondaryContainer = DasaniSecondaryContainer,
    onSecondaryContainer = DasaniSecondary,
    background = DasaniBackground,
    onBackground = DasaniTextPrimary,
    surface = DasaniCardBackground,
    onSurface = DasaniTextPrimary,
    surfaceVariant = DasaniSurfaceVariant,
    onSurfaceVariant = DasaniTextSecondary,
    outline = DasaniCardBorder,
    error = DasaniError
)

@Composable
fun DasaniTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DasaniColorScheme,
        typography = Typography,
        content = content
    )
}
