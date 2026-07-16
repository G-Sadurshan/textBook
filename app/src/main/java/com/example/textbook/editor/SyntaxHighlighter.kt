package com.example.textbook.editor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

object SyntaxHighlighter {

    private val kotlinKeywords = setOf(
        "package", "import", "class", "interface", "fun", "val", "var", "if", "else", "for", "while",
        "return", "when", "is", "in", "as", "try", "catch", "finally", "throw", "object", "typealias",
        "typeof", "yield", "by", "get", "set", "private", "public", "protected", "internal", "override",
        "open", "abstract", "enum", "data", "sealed", "annotation", "constructor", "init", "suspend", "tailrec"
    )

    fun highlightKotlin(code: String): AnnotatedString {
        return buildAnnotatedString {
            var index = 0
            val regex = Regex("(\\b\\w+\\b)|(\".*?\")|('.*?')|(/\\*.*?\\*/)|(//.*)|(\\d+)")
            
            val matches = regex.findAll(code)
            var lastIndex = 0
            
            for (match in matches) {
                append(code.substring(lastIndex, match.range.first))
                
                val word = match.value
                when {
                    word in kotlinKeywords -> {
                        withStyle(style = SpanStyle(color = Color(0xFF9C27B0), fontWeight = FontWeight.Bold)) {
                            append(word)
                        }
                    }
                    word.startsWith("\"") || word.startsWith("'") -> {
                        withStyle(style = SpanStyle(color = Color(0xFF4CAF50))) {
                            append(word)
                        }
                    }
                    word.startsWith("//") || word.startsWith("/*") -> {
                        withStyle(style = SpanStyle(color = Color.Gray)) {
                            append(word)
                        }
                    }
                    word.all { it.isDigit() } -> {
                        withStyle(style = SpanStyle(color = Color(0xFF2196F3))) {
                            append(word)
                        }
                    }
                    else -> {
                        append(word)
                    }
                }
                lastIndex = match.range.last + 1
            }
            append(code.substring(lastIndex))
        }
    }

    fun highlightMarkdown(text: String): AnnotatedString {
        return buildAnnotatedString {
            // Very basic Markdown highlighting
            val lines = text.lines()
            lines.forEachIndexed { i, line ->
                when {
                    line.startsWith("#") -> {
                        withStyle(style = SpanStyle(color = Color(0xFF2196F3), fontWeight = FontWeight.Bold)) {
                            append(line)
                        }
                    }
                    line.startsWith(">") -> {
                        withStyle(style = SpanStyle(color = Color.Gray)) {
                            append(line)
                        }
                    }
                    line.contains("`") -> {
                        // Highlight inline code
                        append(line) // For simplicity, just append. Better regex needed for sub-styles
                    }
                    else -> {
                        append(line)
                    }
                }
                if (i < lines.size - 1) append("\n")
            }
        }
    }
}
