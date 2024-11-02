package com.zestxx.yacupcontest

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.copy
import com.zestxx.yacupcontest.ui.theme.Colors
import com.zestxx.yacupcontest.util.UndoManager

class CanvasState() {
    private val path = Path()
    var mode by mutableStateOf(Mode.DRAW)
    var color by mutableStateOf(Colors.palette.first())
    var lineWidth by mutableFloatStateOf(Constants.MAX_LINE_WIDTH / 2)

    var canvasPathList by mutableStateOf<List<DrawablePath>>(emptyList())
    var currentPath by mutableStateOf<DrawablePath?>(DrawablePath(Path()))
    val undoManager = UndoManager()

    fun setCanvasPath(pathList: List<DrawablePath>) {
        canvasPathList = undoManager.setSteps(pathList)
    }

    fun updateLine(x: Float, y: Float) {
        path.lineTo(x, y)
        currentPath = currentPath?.update(path.copy())
    }

    fun saveStep() {
        currentPath?.let {
            canvasPathList = undoManager.saveStep(it)
        }
        path.reset()
        currentPath = null
    }

    fun undo() {
        canvasPathList = emptyList()
        canvasPathList = undoManager.undo()
    }

    fun redo() {
        canvasPathList = emptyList()
        canvasPathList = undoManager.redo()
    }

    fun startPoint(x: Float, y: Float) {
        createNewPath()
        path.moveTo(x, y)
    }

    fun clear() {
        canvasPathList = emptyList()
        undoManager.reset()
    }

    private fun createNewPath() {
        val blendMode = if (mode == Mode.DRAW) {
            BlendMode.SrcOver
        } else {
            BlendMode.Clear
        }
        currentPath = DrawablePath(path, color, lineWidth, blendMode)
    }
}

@Composable
fun rememberCanvasState(): CanvasState {
    return remember { CanvasState() }
}
