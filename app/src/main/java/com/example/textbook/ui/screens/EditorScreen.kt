package com.example.textbook.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun EditorScreen(navController: NavController, filePath: String) {
    var text by remember { mutableStateOf("package com.scriptflow\n\nclass FileManager {\n    private val repo = Repository()\n\n    fun saveFile(file: File): Boolean {\n        if (file.name.isEmpty()) return false\n        repo.save(file)\n        return true\n    }\n}") }

    Scaffold(
        topBar = {
            EditorTopBar(navController, filePath)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Save */ }, containerColor = Color(0xFF2196F3)) {
                Icon(Icons.Default.Save, contentDescription = "Save", tint = Color.White)
            }
        },
        bottomBar = {
            EditorBottomBar()
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            FileTab(filePath)
            EditorArea(text) { text = it }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorTopBar(navController: NavController, filePath: String) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = { /* Undo */ }) { Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo") }
            IconButton(onClick = { /* Search */ }) { Icon(Icons.Default.Search, contentDescription = "Search") }
            IconButton(onClick = { /* Link */ }) { Icon(Icons.Default.Link, contentDescription = "Link") }
            IconButton(onClick = { /* Preview */ }) { Icon(Icons.Default.Visibility, contentDescription = "Preview") }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Surface(
                color = Color(0xFFE3F2FD),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "HL",
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                    fontSize = 10.sp,
                    color = Color(0xFF2196F3)
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Surface(
                color = Color(0xFFE8F5E9),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "Saved",
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                    fontSize = 10.sp,
                    color = Color(0xFF4CAF50)
                )
            }
        }
    )
}

@Composable
fun FileTab(fileName: String) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(8.dp).background(Color(0xFFFFA500), CircleShape))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = if(fileName.isEmpty()) "Main.kt" else fileName, style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.width(8.dp))
        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
    }
}

@Composable
fun EditorArea(text: String, onTextChange: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxSize()) {
        // Line Numbers
        Column(
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight()
                .background(Color(0xFFFAFAFA))
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val lines = text.split("\n").size
            for (i in 1..lines) {
                Text(text = i.toString(), fontSize = 12.sp, color = Color.LightGray)
            }
        }
        
        // Editor
        TextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.fillMaxSize(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            textStyle = LocalTextStyle.current.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp
            )
        )
    }
}

@Composable
fun EditorBottomBar() {
    Surface(
        color = Color(0xFFF5F5F5),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = "Ln 1", fontSize = 10.sp, color = Color.Gray)
                Text(text = "Col 1", fontSize = 10.sp, color = Color.Gray)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = "UTF-8", fontSize = 10.sp, color = Color.Gray)
                Text(text = "Kotlin", fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}
