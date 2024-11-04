package com.zestxx.yacupcontest.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.zestxx.yacupcontest.models.Frame
import kotlin.collections.isNotEmpty
import kotlin.collections.toList
import kotlin.ranges.until

@Stable
class FramesManager(private val canvasState: CanvasState) {
    var activeFrameIndex by mutableIntStateOf(0)
    val frames = mutableStateListOf<Frame>()
    var backFrame by mutableStateOf<Frame?>(null)
        private set

    val frameCount by derivedStateOf {
        frames.size
    }

    val isNewFrame
        get() = activeFrameIndex == frames.size

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

    fun saveFrame(explicit: Boolean = true) {
        val newFrame = canvasState.createFrame()
        frames.add(newFrame)
        canvasState.clear()
        if (explicit) {
            activeFrameIndex = frames.size
            updateState()
        }
    }

    fun dropFrame() {
        if (activeFrameIndex >= frames.size) return
        frames.removeAt(activeFrameIndex)
        activeFrameIndex = frames.size
        updateState()
    }

    fun copyFrame() {
        saveChanges()
        val currentFrame = runCatching {
            frames[activeFrameIndex]
        }.getOrNull()

        val newFrame = if (currentFrame == null) {
            Frame(canvasState.allCanvasPath.toList())
        } else {
            Frame(currentFrame.data.toList())
        }
        frames.add(activeFrameIndex, newFrame)
        activeFrameIndex += 1
        updateState()
    }

    fun saveChanges() {
        when {
            activeFrameIndex == frames.size
                    && canvasState.allCanvasPath.isNotEmpty() -> saveFrame(false)

            else -> runCatching { updateFrame(activeFrameIndex) }
        }
    }

    fun clearAll() {
        canvasState.clear()
        frames.clear()
        updateState()
    }

    private fun updateState() {
        canvasState.setCanvasPath(getActualFrame()?.data ?: emptyList())
        backFrame = getPreviousFrame()
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