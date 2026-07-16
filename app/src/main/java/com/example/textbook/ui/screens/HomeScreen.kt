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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.textbook.ui.Screen

@Composable
fun HomeScreen(navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            HeaderSection()
        }
        item {
            SearchSection()
        }
        item {
            QuickActionsSection(navController)
        }
        item {
            Text(
                text = "Recent Files",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        items(recentFiles) { file ->
            RecentFileItem(file, onClick = {
                navController.navigate(Screen.Editor.createRoute(file.name))
            })
        }
        item {
            StatsCard()
        }
    }
}

@Composable
fun HeaderSection() {
    Column {
        Text(
            text = "Good morning 👋",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = "Textbook",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SearchSection() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Search files...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFFF5F5F5),
            focusedContainerColor = Color(0xFFF5F5F5),
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun QuickActionsSection(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        QuickActionItem("New", Icons.Default.Add, Color(0xFFE3F2FD), Color(0xFF2196F3)) {}
        QuickActionItem("Open", Icons.Default.FolderOpen, Color(0xFFF3E5F5), Color(0xFF9C27B0)) {}
        QuickActionItem("Recent", Icons.Default.AccessTime, Color(0xFFFFF3E0), Color(0xFFFF9800)) {}
        QuickActionItem("History", Icons.Default.History, Color(0xFFE8F5E9), Color(0xFF4CAF50)) {
            navController.navigate(Screen.History.route)
        }
    }
}

@Composable
fun QuickActionItem(label: String, icon: ImageVector, bgColor: Color, iconColor: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(bgColor)
        ) {
            Icon(icon, contentDescription = label, tint = iconColor)
        }
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}

data class RecentFile(val name: String, val type: String, val lastModified: String)
val recentFiles = listOf(
    RecentFile("Main.kt", "Kotlin", "2m ago"),
    RecentFile("README.md", "Markdown", "1h ago"),
    RecentFile("Utils.kt", "Kotlin", "3h ago")
)

@Composable
fun RecentFileItem(file: RecentFile, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFFF3E0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFFFF9800))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = file.name, fontWeight = FontWeight.Bold)
                Text(text = "${file.type} • ${file.lastModified}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun StatsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2196F3))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "This week", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                StatItem("12", "Files")
                StatItem("8", "Commits")
                StatItem("3", "Projects")
            }
        }
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column {
        Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(text = label, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
    }
}
