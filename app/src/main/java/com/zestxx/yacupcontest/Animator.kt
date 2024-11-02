package com.zestxx.yacupcontest

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class Animator {

    companion object {
        private const val SECOND_IN_MS = 1000L
    }

    var fps: Int = 3
    private var playingJob: Job? = null
    private val _frameFlow = MutableStateFlow<Frame?>(null)
    val frameFlow = _frameFlow.asStateFlow().filterNotNull()
    var isPlaying by mutableStateOf(false)

    fun play(
        frames: List<Frame>,
        coroutineScope: CoroutineScope,
        autoRepeat: Boolean = true
    ) {
        playingJob = coroutineScope.launch {
            isPlaying = true
            do {
                frames.forEach { frame ->
                    _frameFlow.update { frame }
                    delay(SECOND_IN_MS / fps)
                }
            } while (autoRepeat)
        }
    }

    fun stop() {
        isPlaying = false
        playingJob?.cancel()
    }
}