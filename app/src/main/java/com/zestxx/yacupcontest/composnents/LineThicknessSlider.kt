package com.zestxx.yacupcontest.composnents

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.zestxx.yacupcontest.ui.theme.Colors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LineThicknessSlider(
    thickness: Float,
    onThicknessChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    minThickness: Float = 5f,
    maxThickness: Float = 30f
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        // Градиентный слайдер с кастомным стилем
        Slider(
            value = thickness,
            onValueChange = onThicknessChange,
            valueRange = minThickness..maxThickness,
            colors = SliderDefaults.colors(
                thumbColor = Colors.White,
                activeTrackColor = Color.Transparent,
                inactiveTrackColor = Color.Transparent
            ),
            thumb = {
                SliderDefaults.Thumb(
                    interactionSource = remember { MutableInteractionSource() },
                    thumbSize = DpSize(30.dp, 30.dp),
                    colors = SliderDefaults.colors().copy(thumbColor = Colors.Orange)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .zIndex(1F)
        )
        BatShape()
    }
}

@Preview
@Composable
private fun Preview() {
    val thickness = remember { mutableStateOf(0F) }
    LineThicknessSlider(
        thickness = thickness.value,
        { thickness.value = it },
        Modifier.height(50.dp)
    )
}


@Composable
fun BatShape(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        drawBatShape()
    }
}

private fun DrawScope.drawBatShape() {
    val batWidth = size.width
    val batHeight = size.height

    // Начальная ширина "ручки" биты

    // Градиент от зеленого к светло-зеленому
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(
            Colors.LimeGreen,
            Colors.White
        )
    )
    // Постепенное сужение к концу "биты"
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
