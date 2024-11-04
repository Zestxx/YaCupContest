package com.zestxx.yacupcontest

import androidx.compose.ui.graphics.Color


interface UiAction
data object Empty : UiAction
data object PlayClick : UiAction
data object StopClick : UiAction
data object UndoClick : UiAction
data object RedoClick : UiAction
data object AddNewClick : UiAction
data object DeleteClick : UiAction
data object LayersClick : UiAction
data object PencilClick : UiAction
data object BrushClick : UiAction
data object EraserClick : UiAction
data object ShapesClick : UiAction
data object ColorsClick : UiAction
data object FullPalletClick : UiAction
data class ColorSelected(val color: Color) : UiAction
