package com.example.textbook.editor

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class SyntaxHighlightTransformation(val extension: String) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val highlighted = when (extension.lowercase()) {
            "kt" -> SyntaxHighlighter.highlightKotlin(text.text)
            "md" -> SyntaxHighlighter.highlightMarkdown(text.text)
            else -> text
        }
        return TransformedText(highlighted, OffsetMapping.Identity)
    }
}
