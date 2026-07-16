package com.example.textbook.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
fun NewFileScreen(navController: NavController) {
    var fileName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Kotlin") }
    var selectedTemplate by remember { mutableStateOf("Blank") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New File", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp)) {
            Text("File Name", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = fileName,
                onValueChange = { fileName = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. MainActivity") },
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = Color.LightGray) },
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("File Type", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FileTypeChip("Kotlin", selectedType == "Kotlin") { selectedType = "Kotlin" }
                FileTypeChip("Markdown", selectedType == "Markdown") { selectedType = "Markdown" }
                FileTypeChip("Plain Text", selectedType == "Plain Text") { selectedType = "Plain Text" }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Template", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TemplateItem("Blank", selectedTemplate == "Blank") { selectedTemplate = "Blank" }
                TemplateItem("Activity", selectedTemplate == "Activity") { selectedTemplate = "Activity" }
                TemplateItem("ViewModel", selectedTemplate == "ViewModel") { selectedTemplate = "ViewModel" }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { /* Create */ },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                ) {
                    Text("Create File")
                }
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(0.5f).height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
fun FileTypeChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (selected) Color(0xFFFF6D00) else Color.White,
        border = if (selected) null else BorderStroke(1.dp, Color.LightGray)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (selected) Color.White else Color.Gray,
            fontSize = 12.sp
        )
    }
}

@Composable
fun TemplateItem(label: String, selected: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = if (selected) BorderStroke(1.dp, Color(0xFF2196F3)) else BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Default.Description, contentDescription = null, tint = if (selected) Color(0xFF2196F3) else Color.LightGray)
            Text(text = label, modifier = Modifier.weight(1f), color = if (selected) Color.Black else Color.Gray)
            if (selected) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF2196F3), modifier = Modifier.size(16.dp))
            }
        }
    }
}
