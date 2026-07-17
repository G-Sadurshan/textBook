package com.example.textbook.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.textbook.editor.SyntaxHighlightTransformation
import com.example.textbook.editor.UndoRedoManager
import com.example.textbook.ui.MainViewModel
import com.example.textbook.ui.Screen
import com.example.textbook.ui.theme.TextBookTheme
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay

@Composable
fun EditorScreen(navController: NavController, viewModel: MainViewModel) {
    val file by viewModel.currentFile.collectAsState()
    val fontSize by viewModel.fontSize.collectAsState(14)
    val recoveryData by viewModel.recoveryData.collectAsState()
    
    val undoRedoManager = remember(file?.path) { UndoRedoManager(file?.content ?: "") }
    
    var showSaveVersionDialog by remember { mutableStateOf(false) }

    // Crash Prevention: Periodic background cache every 10 seconds
    LaunchedEffect(file?.path, file?.isReadOnly) {
        if (file != null && !file!!.isReadOnly) {
            while (true) {
                delay(10000)
                if (undoRedoManager.currentContent != file?.content) {
                    viewModel.cacheForRecovery(undoRedoManager.currentContent)
                }
            }
        }
    }

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
    
    if (showSaveVersionDialog) {
        var versionName by remember { mutableStateOf("Version ${System.currentTimeMillis() % 10000}") }
        var comment by remember { mutableStateOf("") }
        
        AlertDialog(
            onDismissRequest = { showSaveVersionDialog = false },
            title = { Text("Create Named Version") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = versionName,
                        onValueChange = { versionName = it },
                        label = { Text("Version Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = { Text("Comment (Optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.saveFile(undoRedoManager.currentContent, versionName, comment)
                    showSaveVersionDialog = false
                }) {
                    Text("Save Version")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveVersionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    EditorScreenContent(
        fileName = file?.name ?: "No File",
        fileExtension = file?.extension ?: "txt",
        textContent = undoRedoManager.currentContent,
        fontSize = fontSize,
        isReadOnly = file?.isReadOnly ?: false,
        canUndo = undoRedoManager.canUndo(),
        canRedo = undoRedoManager.canRedo(),
        onTextChange = {
            if (file?.isReadOnly != true) {
                undoRedoManager.onContentChange(it)
            }
        },
        onUndo = { undoRedoManager.undo() },
        onRedo = { undoRedoManager.redo() },
        onSaveClick = { 
            viewModel.saveFile(undoRedoManager.currentContent) 
        },
        onSaveVersionClick = {
            showSaveVersionDialog = true
        },
        onToggleReadOnly = { viewModel.toggleReadOnly() },
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
    isReadOnly: Boolean,
    canUndo: Boolean,
    canRedo: Boolean,
    onTextChange: (String) -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onSaveClick: () -> Unit,
    onSaveVersionClick: () -> Unit,
    onToggleReadOnly: () -> Unit,
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
                    IconButton(onClick = onToggleReadOnly) {
                        Icon(
                            if (isReadOnly) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = "Read Only",
                            tint = if (isReadOnly) Color(0xFFE11D48) else Color(0xFF3B82F6)
                        )
                    }
                    IconButton(onClick = onUndo, enabled = canUndo && !isReadOnly) { 
                        Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo") 
                    }
                    IconButton(onClick = onRedo, enabled = canRedo && !isReadOnly) { 
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
                        }, enabled = !isReadOnly) {
                            Icon(Icons.AutoMirrored.Filled.FormatAlignLeft, contentDescription = "Format")
                        }
                    }
                    IconButton(onClick = onSaveClick, enabled = !isReadOnly) { 
                        Icon(Icons.Default.Save, contentDescription = "Save") 
                    }
                    IconButton(onClick = onSaveVersionClick, enabled = !isReadOnly) {
                        Icon(Icons.Default.History, contentDescription = "Save Version")
                    }
                }
            )
        },
        bottomBar = {
            EditorBottomBar(fileExtension, textContent, isReadOnly)
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            EditorArea(textContent, fileExtension, fontSize, isReadOnly, onTextChange)
        }
    }
}

@Composable
fun EditorArea(text: String, extension: String, fontSize: Int, isReadOnly: Boolean, onTextChange: (String) -> Unit) {
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
            readOnly = isReadOnly,
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
fun EditorBottomBar(extension: String, content: String, isReadOnly: Boolean) {
    val charCount = content.length
    val wordCount = if (content.isBlank()) 0 else content.split(Regex("\\s+")).size
    val lineCount = content.lines().size

    Surface(
        color = Color(0xFFF1F5F9),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = "L: $lineCount", fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                Text(text = "W: $wordCount", fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                Text(text = "C: $charCount", fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (isReadOnly) {
                    Text(text = "READ ONLY", fontSize = 10.sp, color = Color(0xFFE11D48), fontWeight = FontWeight.ExtraBold)
                }
                Text(text = "UTF-8", fontSize = 10.sp, color = Color(0xFF64748B))
                Text(text = extension.uppercase(), fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
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
            isReadOnly = false,
            canUndo = true,
            canRedo = false,
            onTextChange = {},
            onUndo = {},
            onRedo = {},
            onSaveClick = {},
            onSaveVersionClick = {},
            onToggleReadOnly = {},
            onBackClick = {},
            onSearchClick = {},
            onPreviewClick = {}
        )
    }
}
