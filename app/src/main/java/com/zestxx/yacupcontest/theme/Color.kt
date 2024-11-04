package com.zestxx.yacupcontest.ui.theme

import androidx.compose.ui.graphics.Color

object Palette {
    val LimeGreen = Color(0xFFA8DB10)
    val White = Color(0xFFFFFFFF)
    val Black = Color(0xFF000000)
    val Blue = Color(0xFF1976D2)
    val Orange = Color(0xFFFF3D00)
    val Gray = Color(0xFF8B8B8B)
}

data class AppColorScheme(
    val background: Color,
    val disabledTint: Color,
    val iconTint: Color,
    val selectedTint: Color
)