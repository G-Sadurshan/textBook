package com.example.textbook.editor

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import java.util.Stack

class UndoRedoManager(initialContent: String) {
    private val undoStack = Stack<String>()
    private val redoStack = Stack<String>()
    
    var currentContent by mutableStateOf(initialContent)
        private set

    fun onContentChange(newContent: String) {
        if (newContent != currentContent) {
            undoStack.push(currentContent)
            redoStack.clear()
            currentContent = newContent
        }
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            redoStack.push(currentContent)
            currentContent = undoStack.pop()
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            undoStack.push(currentContent)
            currentContent = redoStack.pop()
        }
    }

    fun canUndo(): Boolean = undoStack.isNotEmpty()
    fun canRedo(): Boolean = redoStack.isNotEmpty()
}
