package com.example.textbook.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.textbook.domain.TextFile
import com.example.textbook.ui.MainViewModel
import com.example.textbook.ui.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: MainViewModel, onMenuClick: () -> Unit) {
    val recentFiles by viewModel.recentFiles.collectAsState()

    Scaffold(
        topBar = {
            HomeTopBar(onMenuClick)
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(Screen.NewFile.route) },
                containerColor = Color(0xFF3B82F6),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text("New File", modifier = Modifier.padding(start = 8.dp))
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                HeroCard()
            }
            
            item {
                QuickActionGrid(navController)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Continue Working",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { navController.navigate(Screen.Files.route) }) {
                        Text("View All", color = Color(0xFF3B82F6))
                    }
                }
            }

            if (recentFiles.isEmpty()) {
                item {
                    EmptyRecentState()
                }
            } else {
                items(recentFiles.take(4)) { file ->
                    PremiumFileCard(file) {
                        viewModel.openFile(file.path)
                        navController.navigate(Screen.Editor.createRoute(file.path))
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(onMenuClick: () -> Unit) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Rounded.MenuBook,
                    contentDescription = null,
                    tint = Color(0xFF3B82F6),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("TextBook", fontWeight = FontWeight.ExtraBold)
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Rounded.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            IconButton(onClick = { /* Search */ }) { Icon(Icons.Rounded.Search, null) }
            IconButton(onClick = { /* Notifications */ }) { Icon(Icons.Rounded.NotificationsNone, null) }
            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF7C3AED)),
                contentAlignment = Alignment.Center
            ) {
                Text("JD", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}

@Composable
fun HeroCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Gradient Glow
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(150.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFF3B82F6).copy(alpha = 0.3f), Color.Transparent)
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Start Writing\nWithout Limits",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 30.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Create, edit, compare versions\nand recover your work anytime.",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
            
            // Abstract Illustration Placeholder
            Icon(
                Icons.Rounded.AutoMode,
                contentDescription = null,
                tint = Color(0xFF06B6D4).copy(alpha = 0.2f),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .size(100.dp)
            )
        }
    }
}

@Composable
fun QuickActionGrid(navController: NavController) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            QuickActionPremiumItem(Modifier.weight(1f), "Recent", Icons.Rounded.AccessTime, Color(0xFFE0F2FE), Color(0xFF3B82F6)) {
                navController.navigate(Screen.Files.route)
            }
            QuickActionPremiumItem(Modifier.weight(1f), "Kotlin", Icons.Rounded.Code, Color(0xFFF5F3FF), Color(0xFF7C3AED)) {
                // navController.navigate(Screen.Files.route) // or filter
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            QuickActionPremiumItem(Modifier.weight(1f), "Markdown", Icons.Rounded.Description, Color(0xFFECFEFF), Color(0xFF06B6D4)) {
                // Markdown specific
            }
            QuickActionPremiumItem(Modifier.weight(1f), "History", Icons.Rounded.History, Color(0xFFF0FDF4), Color(0xFF10B981)) {
                navController.navigate(Screen.History.route)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            QuickActionPremiumItem(Modifier.weight(1f), "Favorites", Icons.Rounded.Star, Color(0xFFFEF3C7), Color(0xFFF59E0B)) {
                navController.navigate(Screen.Favorites.route)
            }
            QuickActionPremiumItem(Modifier.weight(1f), "Settings", Icons.Rounded.Settings, Color(0xFFF1F5F9), Color(0xFF64748B)) {
                navController.navigate(Screen.Settings.route)
            }
        }
    }
}

@Composable
fun QuickActionPremiumItem(modifier: Modifier, label: String, icon: ImageVector, bgColor: Color, tint: Color, onClick: () -> Unit) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(12.dp))
            Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
        }
    }
}

@Composable
fun PremiumFileCard(file: TextFile, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    when(file.extension.lowercase()) {
                        "kt" -> Icons.Rounded.Code
                        "md" -> Icons.Rounded.Description
                        else -> Icons.Rounded.Article
                    },
                    contentDescription = null,
                    tint = Color(0xFF64748B)
                )
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(file.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                Text(
                    "${file.extension.uppercase()} • Last modified 2h ago", 
                    fontSize = 12.sp, 
                    color = Color(0xFF64748B)
                )
            }
            
            IconButton(onClick = { /* Toggle Favorite */ }) {
                Icon(
                    if (file.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = null,
                    tint = if (file.isFavorite) Color(0xFFF59E0B) else Color(0xFFCBD5E1)
                )
            }
        }
    }
}

@Composable
fun EmptyRecentState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Rounded.AutoMode, null, modifier = Modifier.size(60.dp), tint = Color(0xFFCBD5E1))
        Spacer(Modifier.height(16.dp))
        Text("No recent documents", fontWeight = FontWeight.Medium, color = Color(0xFF94A3B8))
        Text("Your created files will appear here", fontSize = 12.sp, color = Color(0xFFCBD5E1))
    }
}
