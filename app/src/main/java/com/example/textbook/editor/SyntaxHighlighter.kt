package com.example.textbook.editor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

object SyntaxHighlighter {

    private var kotlinKeywords = setOf(
        "package", "import", "class", "interface", "fun", "val", "var", "if", "else", "for", "while",
        "return", "when", "is", "in", "as", "try", "catch", "finally", "throw", "object", "typealias",
        "typeof", "yield", "by", "get", "set", "private", "public", "protected", "internal", "override",
        "open", "abstract", "enum", "data", "sealed", "annotation", "constructor", "init", "suspend", "tailrec"
    )

    fun initialize(keywords: Set<String>) {
        if (keywords.isNotEmpty()) {
            kotlinKeywords = keywords
        }
    }

    private val colors = object {
        val keyword = Color(0xFFD32F2F)
        val string = Color(0xFF388E3C)
        val comment = Color(0xFF757575)
        val number = Color(0xFF1976D2)
        val annotation = Color(0xFFF57C00)
        val function = Color(0xFF7B1FA2)
        val tag = Color(0xFF00796B)
        val attribute = Color(0xFFE64A19)
    }

    private val javaKeywords = setOf(
        "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
        "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
        "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
        "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp",
        "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while"
    )

    private val jsKeywords = setOf(
        "await", "break", "case", "catch", "class", "const", "continue", "debugger", "default", "delete",
        "do", "else", "export", "extends", "finally", "for", "function", "if", "import", "in", "instanceof",
        "new", "return", "super", "switch", "this", "throw", "try", "typeof", "var", "void", "while", "with", "yield",
        "let", "static", "enum"
    )

    fun highlight(code: String, extension: String): AnnotatedString {
        return when (extension.lowercase()) {
            "kt" -> highlightByKeywords(code, kotlinKeywords)
            "java" -> highlightByKeywords(code, javaKeywords)
            "js", "ts" -> highlightByKeywords(code, jsKeywords)
            "json" -> highlightJson(code)
            "xml", "html" -> highlightXml(code)
            "md" -> highlightMarkdown(code)
            else -> AnnotatedString(code)
        }
    }

    private fun highlightByKeywords(code: String, keywords: Set<String>): AnnotatedString {
        return buildAnnotatedString {
            val regex = Regex("(\\b\\w+\\b)|(\".*?\")|('.*?')|(/\\*.*?\\*/)|(//.*)|(\\d+)|(@\\w+)")
            val matches = regex.findAll(code)
            var lastIndex = 0
            
            for (match in matches) {
                append(code.substring(lastIndex, match.range.first))
                val word = match.value
                when {
                    word in keywords -> {
                        withStyle(SpanStyle(color = colors.keyword, fontWeight = FontWeight.Bold)) { append(word) }
                    }
                    word.startsWith("\"") || word.startsWith("'") -> {
                        withStyle(SpanStyle(color = colors.string)) { append(word) }
                    }
                    word.startsWith("//") || word.startsWith("/*") -> {
                        withStyle(SpanStyle(color = colors.comment)) { append(word) }
                    }
                    word.startsWith("@") -> {
                        withStyle(SpanStyle(color = colors.annotation)) { append(word) }
                    }
                    word.all { it.isDigit() } -> {
                        withStyle(SpanStyle(color = colors.number)) { append(word) }
                    }
                    else -> append(word)
                }
                lastIndex = match.range.last + 1
            }
            append(code.substring(lastIndex))
        }
    }

    private fun highlightJson(text: String): AnnotatedString {
        return buildAnnotatedString {
            val regex = Regex("(\".*?\")|(\\d+)|(true|false|null)")
            val matches = regex.findAll(text)
            var lastIndex = 0
            for (match in matches) {
                append(text.substring(lastIndex, match.range.first))
                val value = match.value
                when {
                    value.startsWith("\"") -> {
                        val color = if (text.getOrNull(match.range.last + 1) == ':') colors.attribute else colors.string
                        withStyle(SpanStyle(color = color)) { append(value) }
                    }
                    value.all { it.isDigit() } -> withStyle(SpanStyle(color = colors.number)) { append(value) }
                    value in setOf("true", "false", "null") -> withStyle(SpanStyle(color = colors.keyword)) { append(value) }
                    else -> append(value)
                }
                lastIndex = match.range.last + 1
            }
            append(text.substring(lastIndex))
        }
    }

    private fun highlightXml(text: String): AnnotatedString {
        return buildAnnotatedString {
            val regex = Regex("(<[^>]+>)|(\".*?\")|('.*?')")
            val matches = regex.findAll(text)
            var lastIndex = 0
            for (match in matches) {
                append(text.substring(lastIndex, match.range.first))
                val value = match.value
                when {
                    value.startsWith("<") -> withStyle(SpanStyle(color = colors.tag)) { append(value) }
                    value.startsWith("\"") || value.startsWith("'") -> withStyle(SpanStyle(color = colors.string)) { append(value) }
                    else -> append(value)
                }
                lastIndex = match.range.last + 1
            }
            append(text.substring(lastIndex))
        }
    }

    private fun highlightMarkdown(text: String): AnnotatedString {
        return buildAnnotatedString {
            text.lines().forEachIndexed { i, line ->
                when {
                    line.startsWith("#") -> withStyle(SpanStyle(color = colors.keyword, fontWeight = FontWeight.Bold)) { append(line) }
                    line.startsWith(">") -> withStyle(SpanStyle(color = colors.comment)) { append(line) }
                    line.startsWith("- ") || line.startsWith("* ") -> withStyle(SpanStyle(color = colors.number)) { append(line) }
                    else -> append(line)
                }
                if (i < text.lines().size - 1) append("\n")
            }
        }
    }
}
