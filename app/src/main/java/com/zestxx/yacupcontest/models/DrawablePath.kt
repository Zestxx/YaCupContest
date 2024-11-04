package com.zestxx.yacupcontest.models

import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

class DrawablePath(
    val path: Path,
    val color: Color = Color.Red,
    val lineWidth: Float = 0F,
    val blendMode: BlendMode = BlendMode.SrcOver,
) {
    fun update(
        path: Path,
        color: Color? = null,
        lineWidth: Float? = null,
        blendMode: BlendMode? = null,
    ): DrawablePath {
        return DrawablePath(
            path = path,
            color = color ?: this.color,
            lineWidth = lineWidth ?: this.lineWidth,
            blendMode = blendMode ?: this.blendMode
        )
    }
}