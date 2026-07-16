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

    val allFiles = dao.getAllFiles()

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
}
