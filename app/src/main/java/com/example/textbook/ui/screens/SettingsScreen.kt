package com.example.textbook.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.textbook.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: MainViewModel = hiltViewModel()) {
    val darkMode by viewModel.darkMode.collectAsState(initial = false)
    val fontSize by viewModel.fontSize.collectAsState(initial = 14)
    val wordWrap by viewModel.wordWrap.collectAsState(initial = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { SettingsGroup("APPEARANCE") {
                SettingToggle("Dark Mode", darkMode) { viewModel.setDarkMode(it) }
            }}
            
            item { SettingsGroup("EDITOR") {
                SettingRow("Font Size", "$fontSize px") {
                    // Action for font size
                }
                SettingToggle("Word Wrap", wordWrap) {
                    // Action for word wrap
                }
            }}
        }
    }
}

@Composable
fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(text = title, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun SettingRow(label: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = value, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF2196F3), fontWeight = FontWeight.Medium)
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

@Composable
fun SettingToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
