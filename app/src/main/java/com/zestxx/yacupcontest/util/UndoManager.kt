package com.zestxx.yacupcontest.util

import com.zestxx.yacupcontest.DrawablePath

class UndoManager {
    private val pathStack = mutableListOf<DrawablePath>()
    private val undoStack = mutableListOf<DrawablePath>()

    fun setSteps(pathList: List<DrawablePath>): MutableList<DrawablePath> {
        pathStack.addAll(pathList)
        undoStack.clear()
        return pathStack
    }

    fun saveStep(state: DrawablePath): MutableList<DrawablePath> {
        pathStack.add(state)
        undoStack.clear()
        return pathStack
    }

    fun undo(): MutableList<DrawablePath> {
        if (pathStack.isNotEmpty()) {
            val currentState = pathStack.last()
            undoStack.add(currentState)
            pathStack.removeLast()
        }
        return pathStack
    }

    fun redo(): MutableList<DrawablePath> {
        if (undoStack.isNotEmpty()) {
            val state = undoStack.removeLast()
            pathStack.add(state)
        }
        return pathStack
    }

    fun reset() {
        pathStack.clear()
        undoStack.clear()
    }
}