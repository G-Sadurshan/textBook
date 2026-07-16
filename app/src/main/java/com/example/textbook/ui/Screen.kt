package com.example.textbook.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Files : Screen("files", "Files", Icons.Default.Description)
    object Editor : Screen("editor/{filePath}", "Editor", Icons.Default.Edit) {
        fun createRoute(filePath: String) = "editor/$filePath"
    }
    object History : Screen("history", "History", Icons.Default.History)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object SearchReplace : Screen("search_replace", "Search", Icons.Default.Search)
    object NewFile : Screen("new_file", "New File", Icons.Default.Add)
    object MarkdownPreview : Screen("markdown_preview", "Preview", Icons.Default.Visibility)
}
