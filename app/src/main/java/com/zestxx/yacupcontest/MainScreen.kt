package com.zestxx.yacupcontest

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.zestxx.yacupcontest.ui.theme.Colors

fun Modifier.bindToCanvas(canvasState: CanvasState): Modifier {
    return this.pointerInput(true) {
        detectDragGestures(
            onDragStart = { offset -> canvasState.startPoint(offset.x, offset.y) },
            onDrag = { change, dragAmount ->
                canvasState.updateLine(change.position.x, change.position.y)
            },
            onDragEnd = { canvasState.saveStep() }
        )
    }
}

@Composable
fun Background(@DrawableRes bkgRes: Int, modifier: Modifier = Modifier) {
    Image(
        modifier = modifier,
        painter = painterResource(bkgRes),
        contentDescription = "Canvas",
        contentScale = ContentScale.FillHeight
    )
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val canvasState = rememberCanvasState()
    val stepsManager = remember { StepsManager(canvasState) }
    val animator = remember { Animator(stepsManager) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier
            .fillMaxSize()
            .bindToCanvas(canvasState)
            .onPlaced {
                canvasState.initSize(it.size)
            }
    ) {
        Background(
            R.drawable.bkg_canvas,
            Modifier
                .fillMaxSize()
        )

        AppCanvas(
            modifier = Modifier.fillMaxSize(),
            canvasState = canvasState,
        )

        AnimatedVisibility(!animator.isPlaying, enter = fadeIn(), exit = fadeOut()) {
            BackFrame(
                modifier = Modifier.fillMaxSize(),
                backFrame = stepsManager.backFrame
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = {
                        canvasState.mode = if (canvasState.mode == Mode.DRAW) {
                            Mode.ERASE
                        } else {
                            Mode.DRAW
                        }
                    }
                ) {
                    Text(canvasState.mode.name)
                }
                Spacer(Modifier.width(8.dp))
                Row(Modifier.wrapContentSize(), horizontalArrangement = spacedBy(6.dp)) {
                    Colors.palette.fastForEach { color ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable(onClick = { canvasState.color = color })
                        )
                    }
                }

            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = spacedBy(8.dp)) {
                Button(onClick = { canvasState.undo() }) { Text("Undo") }
                Button(onClick = { canvasState.redo() }) { Text("Redo") }
                Button(onClick = { stepsManager.saveFrame() }) { Text("New") }
                Button(
                    onClick = {
                        if (!animator.isPlaying) {
                            stepsManager.saveChanges()
                            animator.play(coroutineScope)
                        } else {
                            animator.stop()
                        }
                    },
                    enabled = stepsManager.frameCount > 0
                ) {
                    if (!animator.isPlaying) {
                        Text("Play")
                    } else {
                        Text("Stop")
                    }
                }
            }
            Row(horizontalArrangement = spacedBy(8.dp)) {
                Button(
                    onClick = { stepsManager.undo() },
                    enabled = stepsManager.canUndo
                ) {
                    Image(imageVector = Icons.Default.ArrowBack, null)
                }

                Button(
                    onClick = { stepsManager.redo() },
                    enabled = stepsManager.canRedo
                ) {
                    Image(imageVector = Icons.Default.ArrowForward, null)
                }
            }
        }
        var thickness by remember { mutableFloatStateOf(Constants.MAX_LINE_WIDTH / 2) }
//        LineThicknessSlider(
//            thickness = thickness,
//            onThicknessChange = {
//                thickness = it
//                canvasState.lineWidth = Constants.MAX_LINE_WIDTH - it
//            },
//            minThickness = Constants.MIN_LINE_WIDTH,
//            maxThickness = Constants.MAX_LINE_WIDTH,
//            modifier = Modifier
//                .align(Alignment.BottomStart)
//                .padding(horizontal = 40.dp, vertical = 20.dp)
//                .height(30.dp)
//                .fillMaxWidth()
//        )
        FrameList(
            frames = stepsManager.frames,
            frameSize = canvasState.size,
            selected = stepsManager.activeFrameIndex,
            onClick = {
                stepsManager.saveChanges()
                stepsManager.showStep(it)
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .height(100.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
fun FrameList(
    frames: List<Frame>,
    frameSize: IntSize,
    selected: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val frameRatio by mutableFloatStateOf(frameSize.width.toFloat() / frameSize.height.toFloat())
    // Добавляем пустой фрейм для отображения в ленте
    val displayedFrames = frames.plus(Frame())
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
                            width = if (selected == index) 4.dp else 2.dp,
                            color = if (selected == index) Colors.Blue else Colors.Gray
                        )
                        .clickable(onClick = { onClick.invoke(index) })
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

@Composable
fun BackFrame(modifier: Modifier, backFrame: Frame?) {
    Canvas(modifier.graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)) {
        backFrame?.data?.forEach { path ->
            drawPath(
                path = path.path,
                path.color,
                style = Stroke(width = path.lineWidth, cap = StrokeCap.Round),
                blendMode = path.blendMode,
                alpha = 0.4F
            )
        }
    }
}

@Composable
fun AppCanvas(
    modifier: Modifier = Modifier,
    canvasState: CanvasState
) {
    Canvas(modifier.graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)) {
        canvasState.allCanvasPath.forEach { path ->
            drawPath(
                path = path.path,
                path.color,
                style = Stroke(width = path.lineWidth, cap = StrokeCap.Round),
                blendMode = path.blendMode
            )
        }
        canvasState.drawingPath?.let { path ->
            drawPath(
                path = path.path,
                color = path.color,
                style = Stroke(
                    width = path.lineWidth,
                    cap = StrokeCap.Round
                ),
                blendMode = path.blendMode
            )
            drawCircle(
                Colors.Gray,
                radius = path.lineWidth * 0.75F,
                center = path.path.getEndPoint(),
                style = Stroke(width = 12F)
            )
        }
    }
}

fun Path.getEndPoint(): Offset {
    val pathMeasure = android.graphics.PathMeasure(asAndroidPath(), false)
    val length = pathMeasure.length
    val pos = FloatArray(2)
    pathMeasure.getPosTan(length, pos, null)
    return Offset(pos[0], pos[1])
}


@Preview
@Composable
private fun MainScreenPreview() {
    MainScreen()
}
