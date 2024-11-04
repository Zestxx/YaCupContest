package com.zestxx.yacupcontest

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntSize
import com.zestxx.yacupcontest.composnents.ActionHeaderState
import com.zestxx.yacupcontest.composnents.PaletteState
import com.zestxx.yacupcontest.composnents.Tool
import com.zestxx.yacupcontest.composnents.ToolsState
import kotlinx.coroutines.CoroutineScope

class StateManager(
    val canvasState: CanvasState,
    val stepsManager: StepsManager,
    val animator: Animator,
    val coroutineScope: CoroutineScope,
) {
    private var selectedTool by mutableStateOf<Tool>(Tool.Pencil)
    private var isFullPaletteVisible by mutableStateOf(false)

    var isPaletteVisible by mutableStateOf(false)
    var isLayerListVisible by mutableStateOf(false)

    val toolsState by derivedStateOf {
        ToolsState(
            selectedColor = canvasState.color,
            selectedTool = selectedTool,
            isVisible = !animator.isPlaying
        )
    }

    val actionsState by derivedStateOf {
        canvasState.allCanvasPath
        ActionHeaderState(
            isUndoEnabled = canvasState.canUndo,
            isRedoEnabled = canvasState.canRedo,
            isPlayEnabled = !animator.isPlaying && stepsManager.frameCount > 0,
            isStopEnabled = animator.isPlaying,
            isVisible = !animator.isPlaying
        )
    }

    val paletteState by derivedStateOf {
        PaletteState(
            selectedColor = canvasState.color,
            isFullPaletteVisible = isFullPaletteVisible && isPaletteVisible
        )
    }

    fun onAction(action: UiAction) {
        when (action) {
            UndoClick -> canvasState.undo()
            RedoClick -> canvasState.redo()
            PlayClick -> {
                stepsManager.saveChanges()
                animator.play(coroutineScope)
            }

            StopClick -> animator.stop()
            AddNewClick -> stepsManager.saveFrame()
            DeleteClick -> {
                if (canvasState.allCanvasPath.isNotEmpty() && stepsManager.isNewFrame) {
                    canvasState.clear()
                } else {
                    stepsManager.dropFrame()
                }
            }

            LayersClick -> {
                isLayerListVisible = !isLayerListVisible
            }

            PencilClick -> {
                selectedTool = Tool.Pencil
                canvasState.mode = Mode.DRAW
            }

            BrushClick -> {
                selectedTool = Tool.Brush
            }

            EraserClick -> {
                selectedTool = Tool.Eraser
                canvasState.mode = Mode.ERASE
            }

            ShapesClick -> {
                selectedTool = Tool.Shapes
            }

            ColorsClick -> {
                isPaletteVisible = !isPaletteVisible
            }

            FullPalletClick -> {
                isFullPaletteVisible = !isFullPaletteVisible
            }

            is ColorSelected -> {
                canvasState.color = action.color
            }
        }
    }

    fun initCanvasSize(size: IntSize) {
        canvasState.initSize(size)
    }
}

@Composable
fun rememberStateManager(): StateManager {
    val canvasState = remember { CanvasState() }
    val stepsManager = remember { StepsManager(canvasState) }
    val animator = remember { Animator(stepsManager) }
    val coroutineScope = rememberCoroutineScope()
    return remember { StateManager(canvasState, stepsManager, animator, coroutineScope) }
}