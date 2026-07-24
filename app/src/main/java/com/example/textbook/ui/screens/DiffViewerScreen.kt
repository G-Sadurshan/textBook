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
import androidx.compose.runtime.remember
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
    val diffLines = remember(oldContent, newContent) {
        calculateUnifiedDiff(oldContent, newContent)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Comparison", style = MaterialTheme.typography.titleMedium)
                        Text("Unified View", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
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
            }

            if (diffLines.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("No differences found", color = Color.Gray)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(diffLines) { line ->
                        DiffLineItem(line)
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
fun DiffLineItem(line: DiffLine) {
    val bgColor = when (line.type) {
        DiffLineType.ADDED -> Color(0xFFF0FDF4)
        DiffLineType.REMOVED -> Color(0xFFFEF2F2)
        DiffLineType.UNCHANGED -> Color.Transparent
    }
    
    val textColor = when (line.type) {
        DiffLineType.ADDED -> Color(0xFF166534)
        DiffLineType.REMOVED -> Color(0xFF991B1B)
        DiffLineType.UNCHANGED -> Color.DarkGray
    }

    val prefix = when (line.type) {
        DiffLineType.ADDED -> "+"
        DiffLineType.REMOVED -> "-"
        DiffLineType.UNCHANGED -> " "
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(horizontal = 16.dp, vertical = 2.dp)
    ) {
        Text(
            text = prefix,
            modifier = Modifier.width(20.dp),
            color = textColor.copy(alpha = 0.5f),
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = line.content,
            modifier = Modifier.weight(1f),
            color = textColor,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp
        )
    }
}

data class DiffLine(val content: String, val type: DiffLineType)
enum class DiffLineType { ADDED, REMOVED, UNCHANGED }

fun calculateUnifiedDiff(oldContent: String, newContent: String): List<DiffLine> {
    val oldLines = oldContent.lines()
    val newLines = newContent.lines()
    val patch = DiffUtils.diff(oldLines, newLines)
    val deltas = patch.deltas
    
    val result = mutableListOf<DiffLine>()
    var oldIndex = 0
    var newIndex = 0
    
    // This is a simplified unified diff generator
    // It iterates through deltas and intersperses unchanged lines
    for (delta in deltas) {
        // Add unchanged lines before this delta
        while (oldIndex < delta.source.position) {
            result.add(DiffLine(oldLines[oldIndex], DiffLineType.UNCHANGED))
            oldIndex++
            newIndex++
        }
        
        // Add removed lines
        for (line in delta.source.lines) {
            result.add(DiffLine(line, DiffLineType.REMOVED))
            oldIndex++
        }
        
        // Add added lines
        for (line in delta.target.lines) {
            result.add(DiffLine(line, DiffLineType.ADDED))
            newIndex++
        }
    }
    
    // Add remaining unchanged lines
    while (oldIndex < oldLines.size) {
        result.add(DiffLine(oldLines[oldIndex], DiffLineType.UNCHANGED))
        oldIndex++
    }
    
    return result
}
