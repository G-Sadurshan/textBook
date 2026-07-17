package com.example.textbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.textbook.ui.MainViewModel
import com.example.textbook.ui.Screen
import com.example.textbook.ui.screens.*
import com.example.textbook.ui.theme.TextBookTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var showSplash by remember { mutableStateOf(true) }
            
            TextBookTheme {
                if (showSplash) {
                    SplashScreen(onTimeout = { showSplash = false })
                } else {
                    val viewModel: MainViewModel = viewModel()
                    MainScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    val items = listOf(
        Screen.Home,
        Screen.Files,
        Screen.History,
        Screen.Settings
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                Text("TextBook", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.headlineMedium)
                HorizontalDivider()
                items.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(item.title) },
                        selected = false, // Simplified
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(item.route)
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
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
                    val encodedPath = backStackEntry.arguments?.getString("filePath") ?: ""
                    val filePath = Screen.Editor.parsePath(encodedPath)
                    LaunchedEffect(filePath) {
                        if (filePath.isNotEmpty()) viewModel.openFile(filePath)
                    }
                    EditorScreen(navController, viewModel)
                }
                composable(Screen.History.route) { HistoryScreen(navController, viewModel) }
                composable(Screen.Settings.route) { SettingsScreen(navController) }
                composable(Screen.SearchReplace.route) { SearchReplaceScreen(navController, viewModel) }
                composable(Screen.NewFile.route) { NewFileScreen(navController, viewModel) }
                composable(Screen.MarkdownPreview.route) { 
                    val file by viewModel.currentFile.collectAsState()
                    MarkdownPreviewScreen(navController, file?.content ?: "No content") 
                }
                composable(Screen.DiffViewer.route) {
                    val diffData by viewModel.diffData.collectAsState()
                    DiffViewerScreen(navController, diffData?.first ?: "", diffData?.second ?: "")
                }
            }
        }
    }
}
