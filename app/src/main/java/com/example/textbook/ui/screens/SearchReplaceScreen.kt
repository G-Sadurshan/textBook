package com.example.textbook.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchReplaceScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("repository") }
    var replaceQuery by remember { mutableStateOf("repo") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search & Replace", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = replaceQuery,
                onValueChange = { replaceQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Replace...") },
                leadingIcon = { Icon(Icons.Default.SwapHoriz, contentDescription = null) },
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = true, onClick = {}, label = { Text("Aa Case") })
                FilterChip(selected = false, onClick = {}, label = { Text(".* Regex") })
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {}) { Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Prev", modifier = Modifier.size(16.dp)) }
                IconButton(onClick = {}) { Icon(Icons.Default.ArrowForwardIos, contentDescription = "Next", modifier = Modifier.size(16.dp)) }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = {}, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                    Text("Replace")
                }
                Button(onClick = {}, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))) {
                    Text("Replace All")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("3 results", fontSize = 12.sp, color = Color.Gray)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item { SearchResultItem("Main.kt", 4, "private val repository = Repository()") }
                item { SearchResultItem("Main.kt", 7, ".save(file) repository.save(file)") }
                item { SearchResultItem("Utils.kt", 12, "return .findOrNull(name) repository.findOrNull(name)") }
            }
        }
    }
}

@Composable
fun SearchResultItem(fileName: String, line: Int, content: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = fileName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(text = " line $line", fontSize = 10.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = content, fontSize = 12.sp, color = Color.DarkGray)
        }
    }
}
