package com.example.textbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.textbook.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewFileScreen(navController: NavController, viewModel: MainViewModel = hiltViewModel()) {
    var fileName by remember { mutableStateOf("") }
    var extension by remember { mutableStateOf("txt") }
    val extensions = listOf("txt", "md", "kt", "json", "xml", "java", "csv", "html", "css", "js")
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New File") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = fileName,
                onValueChange = { fileName = it },
                label = { Text("File Name") },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = extension.uppercase(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Extension") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    extensions.forEach { ext ->
                        DropdownMenuItem(
                            text = { Text(ext.uppercase()) },
                            onClick = {
                                extension = ext
                                expanded = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    if (fileName.isNotEmpty()) {
                        viewModel.createFile(fileName, extension, navController.context.filesDir.absolutePath)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = fileName.isNotEmpty()
            ) {
                Text("Create File")
            }
        }
    }
}
