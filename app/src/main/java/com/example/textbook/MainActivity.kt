package com.example.textbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.textbook.ui.MainViewModel
import com.example.textbook.ui.Screen
import com.example.textbook.ui.screens.*
import com.example.textbook.ui.theme.TextBookTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TextBookTheme {
                val viewModel: MainViewModel = viewModel()
                MainScreen(viewModel)
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Home,
        Screen.Files,
        Screen.Editor,
        Screen.History
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen(navController, viewModel) }
            composable(Screen.Files.route) { FilesScreen(navController, viewModel) }
            composable(Screen.Editor.route) { backStackEntry ->
                val filePath = backStackEntry.arguments?.getString("filePath") ?: ""
                LaunchedEffect(filePath) {
                    if (filePath.isNotEmpty()) viewModel.openFile(filePath)
                }
                EditorScreen(navController, viewModel)
            }
            composable(Screen.History.route) { HistoryScreen(navController) }
            composable(Screen.Settings.route) { SettingsScreen(navController) }
            composable(Screen.SearchReplace.route) { SearchReplaceScreen(navController, viewModel) }
            composable(Screen.NewFile.route) { NewFileScreen(navController) }
            composable(Screen.MarkdownPreview.route) { 
                val file by viewModel.currentFile.collectAsState()
                MarkdownPreviewScreen(navController, file?.content ?: "No content") 
            }
        }
    }
}
