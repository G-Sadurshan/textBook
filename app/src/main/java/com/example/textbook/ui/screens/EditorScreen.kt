package com.example.textbook.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.textbook.editor.SyntaxHighlightTransformation
import com.example.textbook.editor.UndoRedoManager
import com.example.textbook.ui.MainViewModel
import com.example.textbook.ui.Screen
import com.example.textbook.ui.theme.TextBookTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun EditorScreen(navController: NavController, viewModel: MainViewModel) {
    val file by viewModel.currentFile.collectAsState()
    val fontSize by viewModel.fontSize.collectAsState(14)
    val recoveryData by viewModel.recoveryData.collectAsState()
    
    // Using a derived state for content to integrate Undo/Redo
    val undoRedoManager = remember(file?.path) { UndoRedoManager(file?.content ?: "") }

    if (recoveryData != null) {
        AlertDialog(
            onDismissRequest = { viewModel.discardRecovery() },
            title = { Text("Recover Unsaved Changes?") },
            text = { Text("It looks like the app closed unexpectedly. Do you want to restore your unsaved work?") },
            confirmButton = {
                Button(onClick = { viewModel.applyRecovery(recoveryData!!) }) {
                    Text("Restore")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.discardRecovery() }) {
                    Text("Discard")
                }
            }
        )
    }

    EditorScreenContent(
        fileName = file?.name ?: "No File",
        fileExtension = file?.extension ?: "txt",
        textContent = undoRedoManager.currentContent,
        fontSize = fontSize,
        canUndo = undoRedoManager.canUndo(),
        canRedo = undoRedoManager.canRedo(),
        onTextChange = {
            undoRedoManager.onContentChange(it)
            viewModel.cacheForRecovery(it)
        },
        onUndo = { undoRedoManager.undo() },
        onRedo = { undoRedoManager.redo() },
        onSaveClick = { 
            viewModel.saveFile(
                undoRedoManager.currentContent, 
                "Version ${System.currentTimeMillis()}", 
                "Manual save"
            ) 
        },
        onBackClick = { navController.popBackStack() },
        onSearchClick = { navController.navigate(Screen.SearchReplace.route) },
        onPreviewClick = { navController.navigate(Screen.MarkdownPreview.route) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreenContent(
    fileName: String,
    fileExtension: String,
    textContent: String,
    fontSize: Int,
    canUndo: Boolean,
    canRedo: Boolean,
    onTextChange: (String) -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onPreviewClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(fileName, style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onUndo, enabled = canUndo) { 
                        Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo") 
                    }
                    IconButton(onClick = onRedo, enabled = canRedo) { 
                        Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo") 
                    }
                    IconButton(onClick = onSearchClick) { 
                        Icon(Icons.Default.Search, contentDescription = "Search") 
                    }
                    if (fileExtension == "md") {
                        IconButton(onClick = onPreviewClick) { 
                            Icon(Icons.Default.Visibility, contentDescription = "Preview") 
                        }
                    }
                    if (fileExtension == "kt") {
                        IconButton(onClick = {
                            val formatted = com.example.textbook.editor.CodeFormatter.formatKotlin(textContent)
                            onTextChange(formatted)
                        }) {
                            Icon(Icons.AutoMirrored.Filled.FormatAlignLeft, contentDescription = "Format")
                        }
                    }
                    IconButton(onClick = onSaveClick) { 
                        Icon(Icons.Default.Save, contentDescription = "Save") 
                    }
                }
            )
        },
        bottomBar = {
            EditorBottomBar(fileExtension, textContent)
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            EditorArea(textContent, fileExtension, fontSize, onTextChange)
        }
    }
}

@Composable
fun EditorArea(text: String, extension: String, fontSize: Int, onTextChange: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxSize()) {
        // Line Numbers
        val lines = text.split("\n").size
        Column(
            modifier = Modifier
                .width(44.dp)
                .fillMaxHeight()
                .background(Color(0xFFFAFAFA))
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (i in 1..lines) {
                Text(text = i.toString(), fontSize = (fontSize - 2).sp, color = Color.LightGray, fontFamily = FontFamily.Monospace)
            }
        }
        
        // Editor
        TextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.fillMaxSize(),
            visualTransformation = SyntaxHighlightTransformation(extension),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            textStyle = LocalTextStyle.current.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = fontSize.sp
            )
        )
    }
}

@Composable
fun EditorBottomBar(extension: String, content: String) {
    val charCount = content.length
    val wordCount = if (content.isBlank()) 0 else content.split(Regex("\\s+")).size
    val lineCount = content.lines().size

    Surface(
        color = Color(0xFFF5F5F5),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = "L: $lineCount", fontSize = 10.sp, color = Color.Gray)
                Text(text = "W: $wordCount", fontSize = 10.sp, color = Color.Gray)
                Text(text = "C: $charCount", fontSize = 10.sp, color = Color.Gray)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = "UTF-8", fontSize = 10.sp, color = Color.Gray)
                Text(text = extension.uppercase(), fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditorScreenPreview() {
    TextBookTheme {
        EditorScreenContent(
            fileName = "MainActivity.kt",
            fileExtension = "kt",
            textContent = "fun main() {\n    println(\"Hello\")\n}",
            fontSize = 14,
            canUndo = true,
            canRedo = false,
            onTextChange = {},
            onUndo = {},
            onRedo = {},
            onSaveClick = {},
            onBackClick = {},
            onSearchClick = {},
            onPreviewClick = {}
        )
    }
}
