package com.example.textbook.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
    
    var versionToRestore by remember { mutableStateOf<FileVersion?>(null) }
    var selectedVersions by remember { mutableStateOf(setOf<FileVersion>()) }

    // Requirement 5: Confirmation dialog before restoring
    if (versionToRestore != null) {
        AlertDialog(
            onDismissRequest = { versionToRestore = null },
            title = { Text("Restore Version") },
            text = { Text("Are you sure you want to restore to v${versionToRestore!!.versionNumber}? This will create a new version with this content.") },
            confirmButton = {
                Button(onClick = {
                    viewModel.restoreVersion(versionToRestore!!)
                    versionToRestore = null
                    // After restore, navigate back to editor to see the changes
                    navController.navigate(Screen.Editor.createRoute(file!!.path)) {
                        popUpTo(Screen.History.route) { inclusive = true }
                    }
                }) {
                    Text("Restore")
                }
            },
            dismissButton = {
                TextButton(onClick = { versionToRestore = null }) {
                    Text("Cancel")
                }
            }
        )
    }

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
                },
                actions = {
                    // Requirement 4: Allow users to compare any two versions
                    if (selectedVersions.size == 2) {
                        IconButton(onClick = {
                            val sorted = selectedVersions.toList().sortedBy { it.versionNumber }
                            viewModel.showDiff(sorted[0], sorted[1])
                            navController.navigate(Screen.DiffViewer.route)
                            selectedVersions = emptySet()
                        }) {
                            Icon(Icons.Default.Difference, contentDescription = "Compare Selected", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (versions.isEmpty()) {
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.History, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Spacer(Modifier.height(16.dp))
                    Text("No versions recorded yet", color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text(
                        "Create a snapshot in the editor to see history here", 
                        color = Color.Gray, 
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 32.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
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
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
                ) {
                    items(versions) { version ->
                        val isSelected = selectedVersions.contains(version)
                        VersionItem(
                            version = version,
                            isSelected = isSelected,
                            onToggleSelect = {
                                if (isSelected) {
                                    selectedVersions = selectedVersions - version
                                } else if (selectedVersions.size < 2) {
                                    selectedVersions = selectedVersions + version
                                }
                            },
                            onView = {
                                viewModel.viewVersion(version)
                                navController.navigate(Screen.Editor.createRoute(file!!.path))
                            },
                            onRestore = { versionToRestore = version },
                            onDiff = {
                                viewModel.showDiff(version)
                                navController.navigate(Screen.DiffViewer.route)
                            }
                        )
                    }
                }
                
                if (selectedVersions.isNotEmpty() && selectedVersions.size < 2) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        tonalElevation = 4.dp
                    ) {
                        Text(
                            "Select another version to compare",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VersionItem(
    version: FileVersion, 
    isSelected: Boolean,
    onToggleSelect: () -> Unit,
    onView: () -> Unit,
    onRestore: () -> Unit, 
    onDiff: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault())
    val timeString = dateFormat.format(Date(version.timestamp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleSelect() }
    ) {
        // Dot on the timeline
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .size(12.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary 
                    else if (version.isFavorite) Color(0xFFFFA500) 
                    else Color(0xFF2196F3)
                )
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isSelected) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp).padding(end = 4.dp))
                        }
                        Text(text = "v${version.versionNumber} - ${version.versionName}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Text(text = timeString, fontSize = 12.sp, color = Color.Gray)
                }
                
                if (!version.comment.isNullOrBlank()) {
                    Text(text = version.comment, fontSize = 14.sp, modifier = Modifier.padding(vertical = 4.dp), color = Color.DarkGray)
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Requirement 6: View (Read-Only)
                    OutlinedButton(
                        onClick = onView,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("View", fontSize = 12.sp)
                    }
                    OutlinedButton(
                        onClick = onRestore,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Restore", fontSize = 12.sp)
                    }
                    OutlinedButton(
                        onClick = onDiff,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Difference, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Diff", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
