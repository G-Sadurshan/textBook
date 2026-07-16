package com.example.textbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.commonmark.parser.Parser
import org.commonmark.renderer.text.TextContentRenderer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkdownPreviewScreen(navController: NavController, content: String) {
    // For a real app, we'd use a more sophisticated renderer or a WebView
    // Here we'll just show the parsed text for demonstration.
    val parser = Parser.builder().build()
    val document = parser.parse(content)
    val renderer = TextContentRenderer.builder().build()
    val renderedText = renderer.render(document)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Markdown Preview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Surface(
                        color = Color(0xFFF5F5F5),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    ) {
                        Row(modifier = Modifier.padding(4.dp)) {
                            Text("Preview", modifier = Modifier.padding(horizontal = 8.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Edit", modifier = Modifier.padding(horizontal = 8.dp), fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                    IconButton(onClick = {}) { Icon(Icons.Default.Share, contentDescription = "Share") }
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
            Text(text = renderedText)
        }
    }
}
