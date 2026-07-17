package com.example.textbook.ui.screens

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
                HeroCard()
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
                    PremiumFileCard(file) {
                        viewModel.openFile(file.path)
                        navController.navigate(Screen.Editor.createRoute(file.path))
                    }
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
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Search your files...", color = Color.Gray, fontSize = 14.sp) },
        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = Color(0xFF3B82F6)) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFFF1F5F9),
            focusedContainerColor = Color(0xFFF1F5F9),
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )
}

@Composable
fun HeroCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(180.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFF3B82F6).copy(alpha = 0.25f), Color.Transparent)
                        )
                    )
            )
            
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .weight(1.2f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Start Writing\nWithout Limits",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 32.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Create, edit, and recover\nyour work with ease.",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
                
                Image(
                    painter = painterResource(id = R.drawable.illustration_workspace),
                    contentDescription = null,
                    modifier = Modifier
                        .weight(1f)
                        .size(150.dp)
                        .padding(end = 12.dp),
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
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            QuickActionPremiumItem(Modifier.weight(1f), "Markdown", Icons.Rounded.Description, Color(0xFFECFEFF), Color(0xFF06B6D4)) {
                viewModel.updateFileSearchQuery(".md")
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
                    "${file.extension.uppercase()} • Last modified", 
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
