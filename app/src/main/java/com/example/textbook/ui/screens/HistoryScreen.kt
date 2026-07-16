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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController) {
    val versions = remember { 
        listOf(
            VersionData("v1.4", "Current", "Added null safety checks", 4, 2, Color(0xFF2196F3)),
            VersionData("v1.3", "Yesterday, 3:10 PM", "Refactored saveFile method", 6, 3, Color(0xFF9C27B0)),
            VersionData("v1.2", "Feb 28, 9:00 AM", "Initial file structure", 14, 0, Color(0xFF4CAF50)),
            VersionData("v1.1", "Feb 27, 4:20 PM", "Initial commit", 50, 0, Color(0xFFFF9800))
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Version History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Main.kt", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
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
                    VersionItem(version)
                }
            }
        }
    }
}

data class VersionData(
    val name: String,
    val time: String,
    val description: String,
    val added: Int,
    val removed: Int,
    val color: Color
)

@Composable
fun VersionItem(version: VersionData) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Dot on the timeline
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .size(12.dp)
                .clip(CircleShape)
                .background(version.color)
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = version.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        if (version.time == "Current") {
                            Surface(
                                modifier = Modifier.padding(start = 8.dp),
                                color = Color(0xFFE3F2FD),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    "Current",
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                    fontSize = 10.sp,
                                    color = Color(0xFF2196F3)
                                )
                            }
                        }
                    }
                    Text(text = version.time, fontSize = 12.sp, color = Color.Gray)
                }
                
                Text(text = version.description, fontSize = 14.sp, modifier = Modifier.padding(vertical = 4.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "+${version.added}", color = Color(0xFF4CAF50), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = "-${version.removed}", color = Color(0xFFF44336), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { /* Restore */ },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Restore", fontSize = 12.sp)
                    }
                    OutlinedButton(
                        onClick = { /* Compare */ },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Compare", fontSize = 12.sp)
                    }
                    IconButton(
                        onClick = { /* Delete */ },
                        modifier = Modifier.size(40.dp).background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp))
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}
