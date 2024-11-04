package com.zestxx.yacupcontest.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.zestxx.yacupcontest.ui.theme.AppColorScheme
import com.zestxx.yacupcontest.ui.theme.Palette

private val darkColorScheme = AppColorScheme(
    background = Palette.Black,
    disabledTint = Palette.Gray,
    iconTint = Palette.White,
    selectedTint = Palette.LimeGreen
)

private val lightColorScheme = AppColorScheme(
    background = Palette.White,
    disabledTint = Palette.Gray,
    iconTint = Palette.Black,
    selectedTint = Palette.LimeGreen
)

@Composable
fun YaCupContestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> darkColorScheme
        else -> lightColorScheme
    }
    CompositionLocalProvider(
        LocalColors provides colorScheme,
    ) {
        MaterialTheme(content = content)
    }
}

object AppTheme {
    val color: AppColorScheme
        @Composable
        get() = LocalColors.current
}

private val LocalColors = staticCompositionLocalOf { lightColorScheme }