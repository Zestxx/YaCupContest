package com.zestxx.yacupcontest

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class StepsManager(private val canvasState: CanvasState) {
    private val frames = mutableListOf<Frame>()
    private var activeFrameIndex = 0
    var backFrame by mutableStateOf<Frame?>(null)

    val canUndo: Boolean
        get() = activeFrameIndex != 0

    val canRedo: Boolean
        get() = activeFrameIndex < frames.size

    fun getFrames(): List<Frame> = frames

    fun getActualFrame(): Frame? {
        return if (activeFrameIndex in 0 until frames.size && frames.isNotEmpty()) {
            frames[activeFrameIndex]
        } else null
    }

    fun getPreviousFrame(): Frame? {
        return if (activeFrameIndex > 0 && frames.isNotEmpty()) {
            frames[activeFrameIndex - 1]
        } else null
    }

    fun undo() {
        saveChanges()
        canvasState.clear()
        activeFrameIndex -= 1
        updateState()
    }

    fun redo() {
        saveChanges()
        canvasState.clear()
        activeFrameIndex += 1
        updateState()
    }

    fun saveFrame(createNew: Boolean = true) {
        if (canvasState.canvasPathList.isEmpty()) return
        val frameIndex = frames.size
        val newFrame = Frame(canvasState.canvasPathList.toList())
        frames.add(frameIndex, newFrame)
        canvasState.clear()
        if (createNew) {
            activeFrameIndex = frames.size
            updateState()
        }
    }

    private fun saveChanges() {
        when {
            activeFrameIndex == frames.size -> saveFrame(false)
            else -> updateFrame(activeFrameIndex)
        }
    }

    private fun updateState() {
        canvasState.setCanvasPath(getActualFrame()?.data ?: emptyList())
        backFrame = getPreviousFrame()
    }

    private fun updateFrame(index: Int) {
        val frame = frames[index]
        val canvasPathList = canvasState.canvasPathList.toList()
        if (frame.data != canvasPathList) {
            frames[index] = Frame(canvasPathList)
        }
        canvasState.clear()
    }
}