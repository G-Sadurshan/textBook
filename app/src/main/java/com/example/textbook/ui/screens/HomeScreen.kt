package com.example.textbook.ui.screens

import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.textbook.R
import com.example.textbook.domain.TextFile
import com.example.textbook.ui.MainViewModel
import com.example.textbook.ui.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: MainViewModel, onMenuClick: () -> Unit) {
    val recentFiles by viewModel.recentFiles.collectAsState()
    val searchQuery by viewModel.fileSearchQuery.collectAsState()
    val filteredFiles by viewModel.filteredFiles.collectAsState()

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
                HeroCard(onStartClick = { navController.navigate(Screen.NewFile.route) })
            }

            item {
                SearchSection(
                    query = searchQuery,
                    onQueryChange = { viewModel.updateFileSearchQuery(it) }
                )
            }
            
            item {
                QuickActionGrid(navController, viewModel)
            }

            val displayFiles = if (searchQuery.isEmpty()) recentFiles.take(4) else filteredFiles

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (searchQuery.isEmpty()) "Continue Working" else "Search Results",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (searchQuery.isEmpty() && recentFiles.isNotEmpty()) {
                        TextButton(onClick = { navController.navigate(Screen.Files.route) }) {
                            Text("View All", color = Color(0xFF3B82F6))
                        }
                    }
                }
            }

            if (displayFiles.isEmpty()) {
                item {
                    if (searchQuery.isEmpty()) EmptyRecentState()
                    else EmptySearchState(searchQuery)
                }
            } else {
                items(displayFiles) { file ->
                    PremiumFileCard(
                        file = file,
                        onFavoriteToggle = { viewModel.toggleFavorite(file) },
                        onClick = {
                            viewModel.openFile(file.path)
                            navController.navigate(Screen.Editor.createRoute(file.path))
                        }
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp))
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
                Text("Textbook", fontWeight = FontWeight.ExtraBold)
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Rounded.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            IconButton(onClick = { /* Notifications */ }) { 
                Icon(Icons.Rounded.NotificationsNone, contentDescription = "Notifications") 
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}

@Composable
fun SearchSection(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        placeholder = { Text("Search your files...", color = Color.Gray, fontSize = 14.sp) },
        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = Color(0xFF3B82F6)) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            unfocusedBorderColor = Color(0xFFE2E8F0),
            focusedBorderColor = Color(0xFF3B82F6)
        ),
        singleLine = true
    )
}

@Composable
fun HeroCard(onStartClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Refined Radial Gradient for a "Nice" Glow
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(220.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF3B82F6).copy(alpha = 0.4f),
                                Color(0xFF7C3AED).copy(alpha = 0.1f),
                                Color.Transparent
                            ),
                            center = Offset(500f, 500f)
                        )
                    )
            )
            
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1.3f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Start Writing\nWithout Limits",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 32.sp
                    )
                    Text(
                        text = "The ultimate mobile workspace\nfor developers.",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = onStartClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Get Started", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                Image(
                    painter = painterResource(id = R.drawable.illustration_workspace),
                    contentDescription = null,
                    modifier = Modifier
                        .weight(1f)
                        .size(140.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
fun QuickActionGrid(navController: NavController, viewModel: MainViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            QuickActionPremiumItem(Modifier.weight(1f), "Recent", Icons.Rounded.AccessTime, Color(0xFFE0F2FE), Color(0xFF3B82F6)) {
                navController.navigate(Screen.Files.route)
            }
            QuickActionPremiumItem(Modifier.weight(1f), "Kotlin", Icons.Rounded.Code, Color(0xFFF5F3FF), Color(0xFF7C3AED)) {
                viewModel.updateFileSearchQuery(".kt")
                navController.navigate(Screen.Files.route)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            QuickActionPremiumItem(Modifier.weight(1f), "Markdown", Icons.Rounded.Description, Color(0xFFECFEFF), Color(0xFF06B6D4)) {
                viewModel.updateFileSearchQuery(".md")
                navController.navigate(Screen.Files.route)
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
fun PremiumFileCard(file: TextFile, onFavoriteToggle: () -> Unit, onClick: () -> Unit) {
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
                    "${file.extension.uppercase()} • Last modified", 
                    fontSize = 12.sp, 
                    color = Color(0xFF64748B)
                )
            }
            
            IconButton(onClick = onFavoriteToggle) {
                Icon(
                    imageVector = if (file.isFavorite) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                    contentDescription = "Toggle Favorite",
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

@Composable
fun EmptySearchState(query: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Rounded.SearchOff, null, modifier = Modifier.size(60.dp), tint = Color(0xFFCBD5E1))
        Spacer(Modifier.height(16.dp))
        Text("No results for \"$query\"", fontWeight = FontWeight.Medium, color = Color(0xFF94A3B8))
        Text("Try checking the spelling or use different keywords", fontSize = 12.sp, color = Color(0xFFCBD5E1), textAlign = TextAlign.Center)
    }
}
