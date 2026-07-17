package com.example.textbook.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.textbook.domain.FileVersion
import com.example.textbook.ui.MainViewModel
import com.example.textbook.ui.Screen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController, viewModel: MainViewModel) {
    val versions by viewModel.versions.collectAsState()
    val file by viewModel.currentFile.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Version History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(file?.name ?: "No File", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (versions.isEmpty()) {
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No versions recorded yet", color = Color.Gray)
            }
        } else {
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                // Timeline line
                Box(modifier = Modifier
                    .padding(start = 31.dp)
                    .fillMaxHeight()
                    .width(2.dp)
                    .background(Color(0xFFEEEEEE))
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(versions) { version ->
                        VersionItem(
                            version = version,
                            onRestore = { viewModel.restoreVersion(version) },
                            onDiff = {
                                viewModel.showDiff(version)
                                navController.navigate(Screen.DiffViewer.route)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VersionItem(version: FileVersion, onRestore: () -> Unit, onDiff: () -> Unit) {
    val dateFormat = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault())
    val timeString = dateFormat.format(Date(version.timestamp))

    Row(modifier = Modifier.fillMaxWidth()) {
        // Dot on the timeline
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .size(12.dp)
                .clip(CircleShape)
                .background(if (version.isFavorite) Color(0xFFFFA500) else Color(0xFF2196F3))
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "v${version.versionNumber} - ${version.versionName}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = timeString, fontSize = 12.sp, color = Color.Gray)
                }
                
                if (!version.comment.isNullOrBlank()) {
                    Text(text = version.comment, fontSize = 14.sp, modifier = Modifier.padding(vertical = 4.dp))
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onRestore,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Restore", fontSize = 12.sp)
                    }
                    OutlinedButton(
                        onClick = onDiff,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Diff", fontSize = 12.sp)
                    }
                    IconButton(
                        onClick = { /* Delete version */ },
                        modifier = Modifier.size(40.dp).background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp))
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}
