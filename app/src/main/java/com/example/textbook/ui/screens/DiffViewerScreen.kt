package com.example.textbook.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
                title = { 
                    Column {
                        Text("Comparison", style = MaterialTheme.typography.titleMedium)
                        Text("Version vs Current", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            // Legend
            Row(
                modifier = Modifier.fillMaxWidth().background(Color(0xFFF8FAFC)).padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DiffLegendItem("Added", Color(0xFFDCFCE7), Color(0xFF166534))
                DiffLegendItem("Removed", Color(0xFFFEE2E2), Color(0xFF991B1B))
                DiffLegendItem("Modified", Color(0xFFFEF3C7), Color(0xFF92400E))
            }

            if (deltas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("No differences found", color = Color.Gray)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    items(deltas) { delta ->
                        DiffDeltaItem(delta.type.name, delta.source.lines, delta.target.lines)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DiffLegendItem(label: String, bgColor: Color, textColor: Color) {
    Surface(color = bgColor, shape = RoundedCornerShape(4.dp)) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun DiffDeltaItem(type: String, sourceLines: List<String>, targetLines: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            when (type) {
                DeltaType.INSERT.name -> {
                    targetLines.forEach { line ->
                        Text(
                            text = "+ $line",
                            modifier = Modifier.fillMaxWidth().background(Color(0xFFF0FDF4)).padding(8.dp),
                            color = Color(0xFF166534),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                }
                DeltaType.DELETE.name -> {
                    sourceLines.forEach { line ->
                        Text(
                            text = "- $line",
                            modifier = Modifier.fillMaxWidth().background(Color(0xFFFEF2F2)).padding(8.dp),
                            color = Color(0xFF991B1B),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                }
                DeltaType.CHANGE.name -> {
                    sourceLines.forEach { line ->
                        Text(
                            text = "- $line",
                            modifier = Modifier.fillMaxWidth().background(Color(0xFFFFFBEB)).padding(8.dp),
                            color = Color(0xFF92400E),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                    targetLines.forEach { line ->
                        Text(
                            text = "+ $line",
                            modifier = Modifier.fillMaxWidth().background(Color(0xFFFFFBEB)).padding(8.dp),
                            color = Color(0xFF92400E),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
