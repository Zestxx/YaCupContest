package com.zestxx.yacupcontest

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class StepsManager(private val canvasState: CanvasState) {
    var activeFrameIndex by mutableIntStateOf(0)
    val frames = mutableStateListOf<Frame>()
    var backFrame by mutableStateOf<Frame?>(null)
        private set

    var frameCount by mutableIntStateOf(0)
        private set

    var canUndo by mutableStateOf(false)
        private set

    var canRedo by mutableStateOf(false)
        private set

    fun getFrames(): List<Frame> = frames

    fun getActualFrame(): Frame? {
        return if (activeFrameIndex in 0 until frames.size && frames.isNotEmpty()) {
            frames[activeFrameIndex]
        } else null
    }

    fun showStep(step: Int) {
        canvasState.clear()
        activeFrameIndex = step
        updateState()
    }

    fun getPreviousFrame(): Frame? {
        return if (activeFrameIndex > 0 && frames.isNotEmpty()) {
            frames[activeFrameIndex - 1]
        } else null
    }

    fun undo() {
        if (activeFrameIndex == 0) return
        saveChanges()
        canvasState.clear()
        activeFrameIndex -= 1
        updateState()
    }

    fun redo() {
        if (activeFrameIndex == frames.size) return
        saveChanges()
        canvasState.clear()
        activeFrameIndex += 1
        updateState()
    }

    fun saveFrame(explicit: Boolean = true) {
        val newFrame = canvasState.createFrame()
        frames.add(newFrame)
        frameCount = frames.size
        canvasState.clear()
        if (explicit) {
            activeFrameIndex = frames.size
            updateState()
        }
    }

    fun dropFrame() {
        frames.removeAt(activeFrameIndex)
        activeFrameIndex = frames.size
        updateState()
    }

    fun copyFrame() {
        val currentFrame = frames[activeFrameIndex]
        val newFrame = Frame(currentFrame.data.toList())
        frames.add(newFrame)
        activeFrameIndex = frames.size
        updateState()
    }

    fun saveChanges() {
        when {
            activeFrameIndex == frames.size && canvasState.allCanvasPath.isNotEmpty() -> {
                saveFrame(false)
            }

            else -> updateFrame(activeFrameIndex)
        }
    }

    private fun updateState() {
        canvasState.setCanvasPath(getActualFrame()?.data ?: emptyList())
        backFrame = getPreviousFrame()
        canUndo = activeFrameIndex != 0
        canRedo = activeFrameIndex < frames.size
    }

    private fun updateFrame(index: Int) {
        if (index < 0 || index == frames.size) return
        val frame = frames[index]
        val canvasPathList = canvasState.allCanvasPath.toList()
        if (frame.data != canvasPathList) {
            frames[index] = Frame(canvasPathList)
        }
        canvasState.clear()
    }
}