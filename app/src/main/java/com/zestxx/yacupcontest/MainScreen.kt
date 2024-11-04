package com.zestxx.yacupcontest

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.zestxx.yacupcontest.composnents.ActionsHeader
import com.zestxx.yacupcontest.composnents.ColorPalette
import com.zestxx.yacupcontest.composnents.FrameList
import com.zestxx.yacupcontest.composnents.LineThicknessSlider
import com.zestxx.yacupcontest.composnents.Tools
import com.zestxx.yacupcontest.ui.theme.Colors

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val stateManager = rememberStateManager()

    Box(
        modifier
            .background(Colors.Black)
            .padding(16.dp)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            ActionsHeader(
                state = stateManager.actionsState,
                onAction = stateManager::onAction,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp),
            )
            Spacer(Modifier.height(32.dp))
            DrawableArea(
                stateManager,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1F)
                    .bindToCanvas(stateManager.canvasState)
                    .onPlaced { stateManager.initCanvasSize(it.size) }
            )
            Spacer(Modifier.height(16.dp))
            Tools(
                state = stateManager.toolsState,
                onAction = stateManager::onAction,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
            )
        }

//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//        ) {
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Button(
//                    onClick = {
//                        canvasState.mode = if (canvasState.mode == Mode.DRAW) {
//                            Mode.ERASE
//                        } else {
//                            Mode.DRAW
//                        }
//                    }
//                ) {
//                    Text(canvasState.mode.name)
//                }
//                Spacer(Modifier.width(8.dp))
//                Row(Modifier.wrapContentSize(), horizontalArrangement = spacedBy(6.dp)) {
//                    Colors.palette.fastForEach { color ->
//                        Box(
//                            modifier = Modifier
//                                .size(36.dp)
//                                .clip(CircleShape)
//                                .background(color)
//                                .clickable(onClick = { canvasState.color = color })
//                        )
//                    }
//                }
//
//            }
//            Spacer(Modifier.height(8.dp))
//            Row(horizontalArrangement = spacedBy(8.dp)) {
//                Button(onClick = { canvasState.undo() }) { Text("Undo") }
//                Button(onClick = { canvasState.redo() }) { Text("Redo") }
//                Button(onClick = { stepsManager.saveFrame() }) { Text("New") }
//                Button(
//                    onClick = {
//                        if (!animator.isPlaying) {
//                            stepsManager.saveChanges()
//                            animator.play(coroutineScope)
//                        } else {
//                            animator.stop()
//                        }
//                    },
//                    enabled = stepsManager.frameCount > 0
//                ) {
//                    if (!animator.isPlaying) {
//                        Text("Play")
//                    } else {
//                        Text("Stop")
//                    }
//                }
//            }
//            Row(horizontalArrangement = spacedBy(8.dp)) {
//                Button(
//                    onClick = { stepsManager.undo() },
//                    enabled = stepsManager.canUndo
//                ) {
//                    Image(imageVector = Icons.Default.ArrowBack, null)
//                }
//
//                Button(
//                    onClick = { stepsManager.redo() },
//                    enabled = stepsManager.canRedo
//                ) {
//                    Image(imageVector = Icons.Default.ArrowForward, null)
//                }
//            }
//        }

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
fun DrawableArea(
    stateManager: StateManager,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        Background(
            R.drawable.bkg_canvas,
            Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(size = 20.dp))
        )

        var thickness by remember { mutableFloatStateOf(Constants.MAX_LINE_WIDTH / 2) }
        LineThicknessSlider(
            thickness = thickness,
            color = stateManager.canvasState.color,
            onThicknessChange = {
                thickness = it
                stateManager.canvasState.lineWidth = Constants.MAX_LINE_WIDTH - it
            },
            minThickness = Constants.MIN_LINE_WIDTH,
            maxThickness = Constants.MAX_LINE_WIDTH,
            modifier = Modifier
                .padding(horizontal = 40.dp, vertical = 20.dp)
                .height(20.dp)
                .fillMaxWidth()
                .zIndex(1F)
        )

        AppCanvas(
            modifier = Modifier.fillMaxSize(),
            canvasState = stateManager.canvasState,
        )

        AnimatedVisibility(!stateManager.animator.isPlaying, enter = fadeIn(), exit = fadeOut()) {
            BackFrame(
                modifier = Modifier.fillMaxSize(),
                backFrame = stateManager.stepsManager.backFrame
            )
        }
        AnimatedVisibility(
            stateManager.isLayerListVisible,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            FrameList(
                stepsManager = stateManager.stepsManager,
                frameSize = stateManager.canvasState.size,
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
            )
        }


        AnimatedVisibility(
            stateManager.isPaletteVisible,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            ColorPalette(
                stateManager.paletteState,
                onAction = stateManager::onAction,
                modifier = modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp)
            )
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
                style = Stroke(
                    width = path.lineWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                ),
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
                style = Stroke(
                    width = path.lineWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                ),
                blendMode = path.blendMode
            )
        }
        canvasState.drawingPath?.let { path ->
            drawPath(
                path = path.path,
                color = path.color,
                style = Stroke(
                    width = path.lineWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
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
