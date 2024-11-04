package com.zestxx.yacupcontest.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.collections.forEach
import kotlin.ranges.until

@Stable
class Animator(private val framesManager: FramesManager) {

    companion object {
        private const val SECOND_IN_MS = 1000L
    }

    var fps: Int = 3
    private var playingJob: Job? = null
    var isPlaying by mutableStateOf(false)

    fun play(
        coroutineScope: CoroutineScope,
        autoRepeat: Boolean = true
    ) {
        playingJob = coroutineScope.launch {
            isPlaying = true
            val frameCount = framesManager.frameCount
            do {
                (0 until frameCount).forEach { index ->
                    framesManager.showStep(index)
                    delay(SECOND_IN_MS / fps)
                }
            } while (autoRepeat)
        }
    }

    fun stop() {
        isPlaying = false
        playingJob?.cancel()
        framesManager.showStep(framesManager.frameCount)
    }
}