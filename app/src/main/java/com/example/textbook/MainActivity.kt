package com.example.textbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
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
            val viewModel: MainViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsState(initial = com.example.textbook.ui.theme.ThemeMode.SYSTEM)
            val dynamicColors by viewModel.dynamicColors.collectAsState(initial = true)
            
            var showSplash by remember { mutableStateOf(true) }
            
            val isDarkTheme = when (themeMode) {
                com.example.textbook.ui.theme.ThemeMode.SYSTEM -> isSystemInDarkTheme()
                com.example.textbook.ui.theme.ThemeMode.LIGHT -> false
                com.example.textbook.ui.theme.ThemeMode.DARK -> true
            }

            TextBookTheme(
                darkTheme = isDarkTheme,
                dynamicColor = dynamicColors
            ) {
                if (showSplash) {
                    SplashScreen(onTimeout = { showSplash = false })
                } else {
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
    
    val drawerItems = listOf(
        Screen.Home,
        Screen.Files,
        Screen.Favorites,
        Screen.History,
        Screen.Storage,
        Screen.Trash,
        Screen.Settings
    )

    val bottomBarItems = listOf(
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
                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(item.title) },
                        selected = false,
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
                    bottomBarItems.forEach { screen ->
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
                composable(Screen.Home.route) { 
                    HomeScreen(navController, viewModel, onMenuClick = {
                        scope.launch { drawerState.open() }
                    }) 
                }
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
                composable(Screen.Favorites.route) { 
                    FilesScreen(navController, viewModel) // Filter logic can be added to viewModel
                }
                composable(Screen.Trash.route) { 
                    FilesScreen(navController, viewModel) // Filter logic can be added to viewModel
                }
                composable(Screen.Storage.route) { 
                    FilesScreen(navController, viewModel) // Placeholder
                }
            }
        }
    }
}
