package com.example.textbook.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Files : Screen("files", "Files", Icons.Default.Folder)
    object Editor : Screen("editor/{filePath}", "Editor", Icons.Default.Edit) {
        fun createRoute(filePath: String) = "editor/${filePath.replace("/", "|")}"
        fun parsePath(path: String) = path.replace("|", "/")
    }
    object History : Screen("history", "History", Icons.Default.History)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object SearchReplace : Screen("search_replace", "Search", Icons.Default.Search)
    object NewFile : Screen("new_file", "New File", Icons.Default.Add)
    object MarkdownPreview : Screen("markdown_preview", "Preview", Icons.Default.Visibility)
    object DiffViewer : Screen("diff_viewer", "Diff", Icons.Default.Difference)
}
