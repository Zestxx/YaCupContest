package com.zestxx.yacupcontest

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Stable
class Animator(private val stepsManager: StepsManager) {

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
            val frameCount = stepsManager.frameCount
            do {
                (0 until frameCount).forEach { index ->
                    stepsManager.showStep(index)
                    delay(SECOND_IN_MS / fps)
                }
            } while (autoRepeat)
        }
    }

    fun stop() {
        isPlaying = false
        playingJob?.cancel()
        stepsManager.showStep(stepsManager.frameCount)
    }
}