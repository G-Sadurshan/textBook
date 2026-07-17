package com.example.textbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.commonmark.node.*
import org.commonmark.parser.Parser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkdownPreviewScreen(navController: NavController, content: String) {
    val parser = Parser.builder().build()
    val document = parser.parse(content)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Markdown Preview") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            MarkdownRenderer(document)
        }
    }
}

@Composable
fun MarkdownRenderer(document: Node) {
    var currentNode = document.firstChild
    while (currentNode != null) {
        val node = currentNode
        when (node) {
            is Heading -> {
                val level = node.level
                val text = (node.firstChild as? Text)?.literal ?: ""
                Text(
                    text = text,
                    fontSize = when (level) {
                        1 -> 24.sp
                        2 -> 20.sp
                        else -> 18.sp
                    },
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            is Paragraph -> {
                MarkdownParagraph(node)
            }
            is BulletList -> {
                MarkdownBulletList(node)
            }
            is FencedCodeBlock -> {
                Surface(
                    color = Color(0xFFF5F5F5),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Text(
                        text = node.literal,
                        modifier = Modifier.padding(12.dp),
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }
            }
        }
        currentNode = node.next
    }
}

@Composable
fun MarkdownParagraph(paragraph: Paragraph) {
    var currentChild = paragraph.firstChild
    while (currentChild != null) {
        val child = currentChild
        when (child) {
            is Text -> {
                Text(text = child.literal, modifier = Modifier.padding(vertical = 4.dp))
            }
            is Emphasis -> {
                Text(text = (child.firstChild as? Text)?.literal ?: "", fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            }
            is StrongEmphasis -> {
                Text(text = (child.firstChild as? Text)?.literal ?: "", fontWeight = FontWeight.Bold)
            }
        }
        currentChild = child.next
    }
}

@Composable
fun MarkdownBulletList(list: BulletList) {
    var currentItem = list.firstChild
    while (currentItem != null) {
        val item = currentItem
        if (item is ListItem) {
            Row(modifier = Modifier.padding(start = 16.dp, top = 2.dp, bottom = 2.dp)) {
                Text("• ")
                val p = item.firstChild as? Paragraph
                if (p != null) {
                    MarkdownParagraph(p)
                }
            }
        }
        currentItem = item.next
    }
}
