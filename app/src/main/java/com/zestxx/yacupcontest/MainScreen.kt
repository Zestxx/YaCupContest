package com.zestxx.yacupcontest

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.zestxx.yacupcontest.composnents.LineThicknessSlider
import com.zestxx.yacupcontest.ui.theme.Colors

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val canvasState = rememberCanvasState()
    val stepsManager = remember { StepsManager(canvasState) }
    val animator = remember { Animator() }
    val coroutineScope = rememberCoroutineScope()
    val playState by animator.frameFlow.collectAsState(null)

    Box(
        modifier
            .fillMaxSize()
            .pointerInput(true) {
                detectDragGestures(
                    onDragStart = { offset -> canvasState.startPoint(offset.x, offset.y) },
                    onDrag = { change, dragAmount ->
                        canvasState.updateLine(change.position.x, change.position.y)
                    },
                    onDragEnd = { canvasState.saveStep() }
                )
            }
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(R.drawable.bkg_canvas),
            contentDescription = "Canvas",
            contentScale = ContentScale.FillHeight
        )

        val canvasPath = if (animator.isPlaying) {
            playState?.data ?: emptyList<DrawablePath>()
        } else {
            canvasState.canvasPathList
        }

        CurrentFrame(
            modifier = Modifier
                .fillMaxSize(),
            pathList = canvasPath,
            actualPath = canvasState.currentPath
        )

        if (!animator.isPlaying) {
            PreviewFrame(
                modifier = Modifier
                    .fillMaxSize(),
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
                            animator.stop()
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
                            animator.play(stepsManager.getFrames(), coroutineScope)
                        } else {
                            animator.stop()
                        }
                    },
                    enabled = stepsManager.getFrames().isNotEmpty()
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
        LineThicknessSlider(
            thickness = thickness,
            onThicknessChange = {
                thickness = it
                canvasState.lineWidth = Constants.MAX_LINE_WIDTH - it
            },
            minThickness = Constants.MIN_LINE_WIDTH,
            maxThickness = Constants.MAX_LINE_WIDTH,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 40.dp, vertical = 20.dp)
                .height(30.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
fun PreviewFrame(modifier: Modifier, backFrame: Frame?) {
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
fun PlayerCanvas(
    modifier: Modifier = Modifier,
    frame: Frame,
) {
    Canvas(modifier.graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)) {
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

@Composable
fun CurrentFrame(
    modifier: Modifier = Modifier,
    pathList: List<DrawablePath>,
    actualPath: DrawablePath?
) {
    Canvas(modifier.graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)) {

        pathList.forEach { path ->
            drawPath(
                path = path.path,
                path.color,
                style = Stroke(width = path.lineWidth, cap = StrokeCap.Round),
                blendMode = path.blendMode
            )
        }
        actualPath?.let { path ->
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
                style = Stroke(width = 8F)
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
