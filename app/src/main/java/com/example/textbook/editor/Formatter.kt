package com.example.textbook.editor

object CodeFormatter {
    fun formatKotlin(code: String): String {
        val lines = code.lines()
        var indentLevel = 0
        val formattedLines = mutableListOf<String>()
        
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) {
                formattedLines.add("")
                continue
            }
            
            if (trimmed.startsWith("}") || trimmed.startsWith(")")) {
                indentLevel = (indentLevel - 1).coerceAtLeast(0)
            }
            
            formattedLines.add("    ".repeat(indentLevel) + trimmed)
            
            if (trimmed.endsWith("{") || trimmed.endsWith("(")) {
                indentLevel++
            }
        }
        
        return formattedLines.joinToString("\n")
    }
}
