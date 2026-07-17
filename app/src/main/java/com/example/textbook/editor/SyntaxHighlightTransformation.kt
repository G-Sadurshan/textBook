package com.example.textbook.editor

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class SyntaxHighlightTransformation(private val extension: String) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            SyntaxHighlighter.highlight(text.text, extension),
            OffsetMapping.Identity
        )
    }
}
