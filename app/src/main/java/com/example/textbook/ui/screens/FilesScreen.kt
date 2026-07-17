package com.example.textbook.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.textbook.domain.TextFile
import com.example.textbook.ui.MainViewModel
import com.example.textbook.ui.Screen

@Composable
fun FilesScreen(navController: NavController, viewModel: MainViewModel) {
    val files by viewModel.allFiles.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.NewFile.route) }, containerColor = Color(0xFF2196F3)) {
                Icon(Icons.Default.Add, contentDescription = "New File", tint = Color.White)
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp)) {
            Text(text = "Explorer", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(text = "Internal Storage", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SearchSection(modifier = Modifier.weight(1f))
                IconButton(onClick = { /* Sort */ }) {
                    Icon(Icons.Default.Sort, contentDescription = "Sort")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (files.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("No files yet", color = Color.Gray)
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(files) { file ->
                        ExplorerFileItem(file) { 
                            viewModel.openFile(file.path)
                            navController.navigate(Screen.Editor.createRoute(file.path)) 
                        }
                    }
                }
            }
            
            StorageSection()
        }
    }
}

@Composable
fun ExplorerFileItem(file: TextFile, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF64B5F6))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = file.name, fontWeight = FontWeight.Bold)
                Text(text = "${file.extension.uppercase()} • ${file.path}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun StorageSection() {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Storage", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(text = "Internal", fontSize = 10.sp, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { 0.48f },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = Color(0xFF2196F3),
            trackColor = Color(0xFFE0E0E0)
        )
    }
}

@Composable
fun SearchSection(modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        modifier = modifier,
        placeholder = { Text("Search...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFFF5F5F5),
            focusedContainerColor = Color(0xFFF5F5F5),
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )
}
