package com.example.textbook.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.github.difflib.DiffUtils
import com.github.difflib.patch.DeltaType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiffViewerScreen(navController: NavController, oldContent: String, newContent: String) {
    val patch = DiffUtils.diff(oldContent.lines(), newContent.lines())
    val deltas = patch.deltas

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Changes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(deltas) { delta ->
                DiffDeltaItem(delta.type.name, delta.source.lines, delta.target.lines)
            }
        }
    }
}

@Composable
fun DiffDeltaItem(type: String, sourceLines: List<String>, targetLines: List<String>) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        when (type) {
            DeltaType.INSERT.name -> {
                targetLines.forEach { line ->
                    Text(
                        text = "+ $line",
                        modifier = Modifier.fillMaxWidth().background(Color(0xFFE8F5E9)).padding(4.dp),
                        color = Color(0xFF2E7D32),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }
            }
            DeltaType.DELETE.name -> {
                sourceLines.forEach { line ->
                    Text(
                        text = "- $line",
                        modifier = Modifier.fillMaxWidth().background(Color(0xFFFFEBEE)).padding(4.dp),
                        color = Color(0xFFC62828),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }
            }
            DeltaType.CHANGE.name -> {
                sourceLines.forEach { line ->
                    Text(
                        text = "- $line",
                        modifier = Modifier.fillMaxWidth().background(Color(0xFFFFF3E0)).padding(4.dp),
                        color = Color(0xFFEF6C00),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }
                targetLines.forEach { line ->
                    Text(
                        text = "+ $line",
                        modifier = Modifier.fillMaxWidth().background(Color(0xFFFFF3E0)).padding(4.dp),
                        color = Color(0xFFEF6C00),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
