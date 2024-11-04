package com.zestxx.yacupcontest.composnents


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.zestxx.yacupcontest.Frame
import com.zestxx.yacupcontest.StepsManager
import com.zestxx.yacupcontest.ui.theme.Colors

@Composable
fun FrameList(
    stepsManager: StepsManager,
    frameSize: IntSize,
    modifier: Modifier = Modifier
) {
    val frames = stepsManager.frames
    val frameRatio by mutableFloatStateOf(frameSize.width.toFloat() / frameSize.height.toFloat())
    // Добавляем пустой фрейм для отображения в ленте
    val displayedFrames = frames.plus(Frame())
    val selected = stepsManager.activeFrameIndex
    Box(
        modifier
            .alpha(0.7F)
            .background(Colors.White)
    )
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = spacedBy(4.dp),
        modifier = modifier.fillMaxSize()
    ) {
        displayedFrames.forEachIndexed { index, frame ->
            item(frame.hashCode()) {
                var frameScaleX by remember { mutableFloatStateOf(0F) }
                var frameScaleY by remember { mutableFloatStateOf(0F) }
                Box(
                    Modifier
                        .fillMaxHeight()
                        .aspectRatio(if (frameRatio > 0) frameRatio else 1F)
                        .onPlaced {
                            val scaleX = it.size.width.toFloat() / frameSize.width.toFloat()
                            frameScaleX = scaleX
                            frameScaleY = it.size.height / frameSize.height.toFloat()
                        }
                        .border(
                            width = if (selected == index) 2.dp else 1.dp,
                            color = if (selected == index) Colors.Orange else Colors.Gray
                        )
                        .clickable(
                            onClick = {
                                stepsManager.saveChanges()
                                stepsManager.showStep(index)
                            }
                        )
                ) {
                    Canvas(
                        Modifier
                            .graphicsLayer {
                                this.scaleX = frameScaleX
                                this.scaleY = frameScaleY
                                compositingStrategy = CompositingStrategy.Offscreen
                            }
                    ) {
                        frame.data.forEach { path ->
                            drawPath(
                                path = path.path,
                                path.color,
                                style = Stroke(width = path.lineWidth, cap = StrokeCap.Round),
                                blendMode = path.blendMode
                            )
                        }
                    }
                }
            }
        }
    }
}