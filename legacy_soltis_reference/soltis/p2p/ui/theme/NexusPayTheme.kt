package com.soltis.p2p.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NexusPayColorScheme = lightColorScheme(
    primary        = YellowPrimary,
    onPrimary      = Color.White,
    secondary      = OrangeRetained,
    onSecondary    = Color.White,
    background     = Color(0xFFFFF8F1), // Fondo crema calido
    surface        = Color.White,
    onBackground   = TextPrimary,
    onSurface      = TextPrimary,
)

@Composable
fun NexusPayTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NexusPayColorScheme,
        content     = content
    )
}
