package com.exchangepro.moviles.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = ExchangePrimary,
    secondary = ExchangeAccent,
    tertiary = ExchangePositive,
    background = ExchangeBg,
    surface = ExchangeSurface,
    surfaceVariant = ExchangeElevated,
    error = ExchangeNegative,
    onPrimary = Color.White,
    onSecondary = ExchangeBg,
    onBackground = ExchangeText,
    onSurface = ExchangeText,
    onSurfaceVariant = ExchangeMuted
)

@Composable
fun ExchangeProTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = AppTypography,
        content = content
    )
}
