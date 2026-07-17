package com.example.textbook.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.textbook.data.*
import com.example.textbook.vcs.VersionControlManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val dao = db.fileDao()

    private val _currentFile = MutableStateFlow<FileEntity?>(null)
    val currentFile: StateFlow<FileEntity?> = _currentFile

    private val _allFilesList = MutableStateFlow<List<FileEntity>>(emptyList())
    val allFilesList: StateFlow<List<FileEntity>> = _allFilesList

    init {
        viewModelScope.launch {
            dao.getAllFiles().collect {
                _allFilesList.value = it
            }
        }
    }

    fun openFile(path: String) {
        viewModelScope.launch {
            _currentFile.value = dao.getFileByPath(path)
        }
    }

    fun saveFile(content: String, versionName: String? = null) {
        val file = _currentFile.value ?: return
        viewModelScope.launch {
            // If a version name is provided, create a version/delta
            if (versionName != null) {
                val delta = withContext(Dispatchers.Default) {
                    VersionControlManager.createDelta(file.content, content, file.name)
                }
                val stats = withContext(Dispatchers.Default) {
                    VersionControlManager.getDiffStats(delta)
                }
                val version = VersionEntity(
                    filePath = file.path,
                    versionName = versionName,
                    timestamp = System.currentTimeMillis(),
                    delta = delta,
                    addedCount = stats.first,
                    removedCount = stats.second
                )
                dao.insertVersion(version)
            }
            
            val updatedFile = file.copy(content = content, lastModified = System.currentTimeMillis())
            dao.insertFile(updatedFile)
            _currentFile.value = updatedFile
            
            // Clear crash recovery after manual save
            dao.clearRecoveryData(file.path)
        }
    }

    fun createFile(name: String, extension: String, path: String) {
        viewModelScope.launch {
            val newFile = FileEntity(
                path = path,
                name = name,
                extension = extension,
                content = "",
                lastModified = System.currentTimeMillis()
            )
            dao.insertFile(newFile)
            _currentFile.value = newFile
        }
    }

    // Crash Recovery periodic cache
    fun cacheForRecovery(content: String) {
        val file = _currentFile.value ?: return
        viewModelScope.launch {
            dao.saveRecoveryData(CrashRecoveryEntity(file.path, content, System.currentTimeMillis()))
        }
    }

    // Search and Replace
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResults = MutableStateFlow<List<Int>>(emptyList())
    val searchResults: StateFlow<List<Int>> = _searchResults

    fun search(query: String) {
        _searchQuery.value = query
        val content = _currentFile.value?.content ?: return
        if (query.isEmpty()) {
            _searchResults.value = emptyList()
            return
        }
        val results = mutableListOf<Int>()
        var index = content.indexOf(query)
        while (index >= 0) {
            results.add(index)
            index = content.indexOf(query, index + 1)
        }
        _searchResults.value = results
    }

    fun replace(oldText: String, newText: String) {
        val file = _currentFile.value ?: return
        val newContent = file.content.replace(oldText, newText)
        saveFile(newContent)
    }
}
