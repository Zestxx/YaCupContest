package com.zestxx.yacupcontest

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.copy
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import com.zestxx.yacupcontest.ui.theme.Colors
import com.zestxx.yacupcontest.util.UndoManager

class CanvasState() {
    private val path = Path()
    private val undoManager = UndoManager()

    val canUndo
        get() = undoManager.canUndo

    val canRedo
        get() = undoManager.canRedo

    var size by mutableStateOf(IntSize(0, 0))
    var mode by mutableStateOf(Mode.DRAW)
    var color by mutableStateOf(Colors.palette.first())
    var lineWidth by mutableFloatStateOf(Constants.MAX_LINE_WIDTH / 2)

    var allCanvasPath by mutableStateOf<List<DrawablePath>>(emptyList())
    var drawingPath by mutableStateOf<DrawablePath?>(DrawablePath(Path()))

    fun initSize(size: IntSize) {
        if (this.size.width == 0 && this.size.height == 0) {
            this.size = size
        }
    }

    fun setCanvasPath(pathList: List<DrawablePath>) {
        allCanvasPath = undoManager.setSteps(pathList)
    }

    fun updateLine(x: Float, y: Float) {
        path.lineTo(x, y)
        drawingPath = drawingPath?.update(path.copy())
    }

    fun saveStep() {
        drawingPath?.let {
            allCanvasPath = undoManager.saveStep(it)
        }
        path.reset()
        drawingPath = null
    }

    fun undo() {
        allCanvasPath = emptyList()
        allCanvasPath = undoManager.undo()
    }

    fun redo() {
        allCanvasPath = emptyList()
        allCanvasPath = undoManager.redo()
    }

    fun startPoint(x: Float, y: Float) {
        path.moveTo(x, y)
        createNewPath()
    }

    fun clear() {
        allCanvasPath = emptyList()
        undoManager.reset()
    }

    fun createFrame(): Frame {
        return Frame(allCanvasPath.toList())
    }

    private fun createNewPath() {
        val blendMode = if (mode == Mode.DRAW) {
            BlendMode.SrcOver
        } else {
            BlendMode.Clear
        }
        drawingPath = DrawablePath(path, color, lineWidth, blendMode)
    }
}

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