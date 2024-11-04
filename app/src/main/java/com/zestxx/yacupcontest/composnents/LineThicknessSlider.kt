package com.zestxx.yacupcontest.composnents

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.zestxx.yacupcontest.ui.theme.Colors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LineThicknessSlider(
    thickness: Float,
    color: Color,
    onThicknessChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    minThickness: Float = 5f,
    maxThickness: Float = 30f
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Slider(
            value = thickness,
            onValueChange = onThicknessChange,
            valueRange = minThickness..maxThickness,
            colors = SliderDefaults.colors(
                activeTrackColor = Color.Transparent,
                inactiveTrackColor = Color.Transparent
            ),
            thumb = {
                SliderDefaults.Thumb(
                    interactionSource = remember { MutableInteractionSource() },
                    thumbSize = DpSize(20.dp, 20.dp),
                    colors = SliderDefaults.colors().copy(thumbColor = Colors.White)
                )
            },
            track = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Colors.Gray.copy(alpha = 0.2F))
                ) {
                    BatShape(color = color)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .zIndex(1F)
        )

    }
}

@Composable
fun BatShape(modifier: Modifier = Modifier, color: Color) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        drawBatShape(color)
    }
}

private fun DrawScope.drawBatShape(startColor: Color) {
    val batWidth = size.width
    val batHeight = size.height
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(
            startColor,
            Colors.White
        )
    )
    drawPath(
        brush = gradientBrush,
        path = Path().apply {
            moveTo(0f, 0F)
            lineTo(batWidth, batHeight / 2)
            lineTo(0f, batHeight)
            close()
        },
        style = Fill
    )
}
