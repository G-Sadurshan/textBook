package com.example.textbook.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    val isViewingVersion by viewModel.isViewingVersion.collectAsState()
    
    val undoRedoManager = remember(file?.path, isViewingVersion) { 
        UndoRedoManager(file?.content ?: "") 
    }
    
    var showSaveVersionDialog by remember { mutableStateOf(false) }
    var showSaveAsDialog by remember { mutableStateOf(false) }

    // Requirement 4: Real-time Auto-save on type (every 5 seconds)
    LaunchedEffect(file?.path, file?.isReadOnly, isViewingVersion) {
        if (file != null && !file!!.isReadOnly && !isViewingVersion) {
            while (true) {
                delay(5000) // Improved to 5 seconds for university requirement
                if (undoRedoManager.currentContent != file?.content) {
                    viewModel.saveFile(undoRedoManager.currentContent) // Auto-save to physical file
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
        val versions by viewModel.versions.collectAsState()
        val nextNumber = (versions.maxByOrNull { it.versionNumber }?.versionNumber ?: 0) + 1
        var versionName by remember { mutableStateOf("Version $nextNumber") }
        var comment by remember { mutableStateOf("") }
        
        AlertDialog(
            onDismissRequest = { showSaveVersionDialog = false },
            title = { Text("Create Snapshot") }, // Requirement 7
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
                        label = { Text("Version Note") }, // Requirement 7
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.saveFile(undoRedoManager.currentContent, versionName, comment)
                    showSaveVersionDialog = false
                }) {
                    Text("Create Version")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveVersionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showSaveAsDialog) {
        var newName by remember { mutableStateOf(file?.name ?: "") }
        var extension by remember { mutableStateOf(file?.extension ?: "txt") }
        
        AlertDialog(
            onDismissRequest = { showSaveAsDialog = false },
            title = { Text("Save As") }, // Requirement 1
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("File Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = extension,
                        onValueChange = { extension = it },
                        label = { Text("Extension") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.saveAs(newName, extension, undoRedoManager.currentContent)
                    showSaveAsDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveAsDialog = false }) {
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
        isViewingVersion = isViewingVersion,
        canUndo = undoRedoManager.canUndo(),
        canRedo = undoRedoManager.canRedo(),
        onTextChange = {
            if (file?.isReadOnly != true && !isViewingVersion) {
                undoRedoManager.onContentChange(it)
            }
        },
        onUndo = { undoRedoManager.undo() },
        onRedo = { undoRedoManager.redo() },
        onSaveClick = { 
            viewModel.saveFile(undoRedoManager.currentContent) 
        },
        onSaveAsClick = {
            showSaveAsDialog = true
        },
        onSaveVersionClick = {
            showSaveVersionDialog = true
        },
        onViewHistoryClick = {
            navController.navigate(Screen.History.route)
        },
        onToggleReadOnly = { viewModel.toggleReadOnly() },
        onBackClick = { 
            if (isViewingVersion) {
                // If viewing a version, maybe we want to go back to the current file?
                // For now, let's just pop back stack.
                navController.popBackStack()
            } else {
                navController.popBackStack()
            }
        },
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
    isViewingVersion: Boolean,
    canUndo: Boolean,
    canRedo: Boolean,
    onTextChange: (String) -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onSaveClick: () -> Unit,
    onSaveAsClick: () -> Unit,
    onSaveVersionClick: () -> Unit,
    onViewHistoryClick: () -> Unit,
    onToggleReadOnly: () -> Unit,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onPreviewClick: () -> Unit
) {
    var showMoreActions by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(fileName, style = MaterialTheme.typography.titleMedium)
                        if (isViewingVersion) {
                            Text("Historical Version (Read-Only)", style = MaterialTheme.typography.labelSmall, color = Color(0xFFE11D48))
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onToggleReadOnly, enabled = !isViewingVersion) {
                        Icon(
                            if (isReadOnly || isViewingVersion) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = "Read Only",
                            tint = if (isReadOnly || isViewingVersion) Color(0xFFE11D48) else Color(0xFF3B82F6)
                        )
                    }
                    IconButton(onClick = onUndo, enabled = canUndo && !isReadOnly && !isViewingVersion) { 
                        Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo") 
                    }
                    IconButton(onClick = onRedo, enabled = canRedo && !isReadOnly && !isViewingVersion) { 
                        Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo") 
                    }
                    
                    Box {
                        IconButton(onClick = { showMoreActions = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More Actions")
                        }
                        DropdownMenu(
                            expanded = showMoreActions,
                            onDismissRequest = { showMoreActions = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Search & Replace") },
                                onClick = { 
                                    showMoreActions = false
                                    onSearchClick() 
                                },
                                leadingIcon = { Icon(Icons.Default.Search, null) }
                            )
                            if (fileExtension == "md") {
                                DropdownMenuItem(
                                    text = { Text("Markdown Preview") },
                                    onClick = { 
                                        showMoreActions = false
                                        onPreviewClick() 
                                    },
                                    leadingIcon = { Icon(Icons.Default.Visibility, null) }
                                )
                            }
                            if (fileExtension == "kt") {
                                DropdownMenuItem(
                                    text = { Text("Format Code") },
                                    onClick = {
                                        showMoreActions = false
                                        val formatted = com.example.textbook.editor.CodeFormatter.formatKotlin(textContent)
                                        onTextChange(formatted)
                                    },
                                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.FormatAlignLeft, null) },
                                    enabled = !isReadOnly && !isViewingVersion
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("Create Snapshot") },
                                onClick = { 
                                    showMoreActions = false
                                    onSaveVersionClick() 
                                },
                                leadingIcon = { Icon(Icons.Default.AddAPhoto, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("View History") },
                                onClick = { 
                                    showMoreActions = false
                                    onViewHistoryClick() 
                                },
                                leadingIcon = { Icon(Icons.Default.History, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Save As") },
                                onClick = { 
                                    showMoreActions = false
                                    onSaveAsClick() 
                                },
                                leadingIcon = { Icon(Icons.Default.SaveAs, null) }
                            )
                        }
                    }

                    IconButton(onClick = onSaveClick, enabled = !isReadOnly && !isViewingVersion) { 
                        Icon(Icons.Default.Save, contentDescription = "Save") 
                    }
                }
            )
        },
        bottomBar = {
            EditorBottomBar(fileExtension, textContent, isReadOnly || isViewingVersion)
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (isViewingVersion) {
                Surface(
                    color = Color(0xFFFFF1F2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Viewing old version. Restore to edit.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF9F1239)
                        )
                    }
                }
            }
            EditorArea(textContent, fileExtension, fontSize, isReadOnly || isViewingVersion, onTextChange)
        }
    }
}

@Composable
fun EditorArea(text: String, extension: String, fontSize: Int, isReadOnly: Boolean, onTextChange: (String) -> Unit) {
    val scrollState = rememberScrollState()
    
    Row(modifier = Modifier.fillMaxSize()) {
        // Line Numbers (Requirement 13 improvement)
        val lines = text.split("\n").size
        Column(
            modifier = Modifier
                .width(44.dp)
                .fillMaxHeight()
                .background(Color(0xFFFAFAFA))
                .verticalScroll(scrollState)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (i in 1..lines) {
                Text(
                    text = i.toString(), 
                    fontSize = (fontSize - 2).sp, 
                    color = Color.LightGray, 
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.height(24.dp) // Fixed height to match TextField line height roughly
                )
            }
        }
        
        // Editor
        TextField(
            value = text,
            onValueChange = onTextChange,
            readOnly = isReadOnly,
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
            visualTransformation = SyntaxHighlightTransformation(extension),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            textStyle = LocalTextStyle.current.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = fontSize.sp,
                lineHeight = 24.sp // Set explicit line height to match line numbers
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
            isViewingVersion = false,
            canUndo = true,
            canRedo = false,
            onTextChange = {},
            onUndo = {},
            onRedo = {},
            onSaveClick = {},
            onSaveAsClick = {},
            onSaveVersionClick = {},
            onViewHistoryClick = {},
            onToggleReadOnly = {},
            onBackClick = {},
            onSearchClick = {},
            onPreviewClick = {}
        )
    }
}
