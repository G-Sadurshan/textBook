package com.example.textbook.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.textbook.domain.TextFile
import com.example.textbook.domain.FileVersion
import com.example.textbook.domain.TextBookRepository
import com.example.textbook.core.settings.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: TextBookRepository,
    private val settingsManager: SettingsManager,
) : ViewModel() {

    init {
        Timber.d("MainViewModel initialized")
    }

    private val _currentFile = MutableStateFlow<TextFile?>(null)
    val currentFile: StateFlow<TextFile?> = _currentFile.asStateFlow()

    private val _isViewingVersion = MutableStateFlow(false)
    val isViewingVersion: StateFlow<Boolean> = _isViewingVersion.asStateFlow()

    val allFiles = repository.getAllFiles()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val recentFiles = repository.getRecentFiles()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val favoriteFiles = repository.getFavoriteFiles()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        
    val pinnedFiles = repository.getPinnedFiles()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val versions = _currentFile
        .distinctUntilChanged { old, new -> old?.path == new?.path }
        .flatMapLatest { file ->
            if (file != null) repository.getVersionsForFile(file.path)
            else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun refreshVersions() {
        val file = _currentFile.value ?: return
        viewModelScope.launch {
            // Force a refresh by re-opening the file
            openFile(file.path)
            Timber.d("Manual versions refresh triggered for ${file.path}")
        }
    }

    // Settings
    val themeMode = settingsManager.themeMode
    val dynamicColors = settingsManager.dynamicColors
    val fontSize = settingsManager.fontSize
    val wordWrap = settingsManager.wordWrap

    private val _recoveryData = MutableStateFlow<String?>(null)
    val recoveryData = _recoveryData.asStateFlow()

    fun openFile(path: String) {
        viewModelScope.launch {
            try {
                val file = repository.getFileByPath(path)
                _currentFile.value = file
                _isViewingVersion.value = false // Reset viewing mode
                // Check for recovery
                val recovery = repository.getRecoveryData(path)
                if (recovery != null && recovery != file?.content) {
                    _recoveryData.value = recovery
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to open file $path")
            }
        }
    }

    fun applyRecovery(content: String) {
        _currentFile.value = _currentFile.value?.copy(content = content)
        _recoveryData.value = null
        saveFile(content, "Recovered Content")
    }

    fun discardRecovery() {
        val path = _currentFile.value?.path ?: return
        viewModelScope.launch {
            repository.clearRecoveryData(path)
            _recoveryData.value = null
        }
    }

    fun toggleReadOnly() {
        if (_isViewingVersion.value) return // Requirement 6: Cannot unlock historical versions
        val file = _currentFile.value ?: return
        viewModelScope.launch {
            val updated = file.copy(isReadOnly = !file.isReadOnly)
            repository.saveFile(updated)
            _currentFile.value = updated
        }
    }

    fun saveFile(content: String, versionName: String? = null, comment: String? = null) {
        val file = _currentFile.value ?: return
        if (file.isReadOnly && !_isViewingVersion.value) return
        
        viewModelScope.launch {
            try {
                // If it's a version save, we store the diff
                if (versionName != null) {
                    repository.createVersion(file.path, versionName, comment, content)
                }
                
                // Always update the base file and the UI state
                val updatedFile = file.copy(content = content, lastModified = System.currentTimeMillis())
                repository.saveFile(updatedFile)
                _currentFile.value = updatedFile
                repository.clearRecoveryData(file.path)
            } catch (e: Exception) {
                Timber.e(e, "Failed to save file ${file.path}")
            }
        }
    }

    fun saveAs(name: String, extension: String, content: String) {
        val currentFile = _currentFile.value ?: return
        val parentDir = java.io.File(currentFile.path).parent ?: return
        viewModelScope.launch {
            try {
                val newPath = "$parentDir/$name.$extension"
                val newFile = TextFile(
                    path = newPath,
                    name = name,
                    extension = extension,
                    content = content,
                    lastModified = System.currentTimeMillis()
                )
                repository.saveFile(newFile)
                openFile(newPath)
            } catch (e: Exception) {
                Timber.e(e, "Failed to save as $name")
            }
        }
    }

    fun renameFile(file: TextFile, newName: String) {
        viewModelScope.launch {
            try {
                val oldPath = file.path
                val oldFile = java.io.File(oldPath)
                val newPath = "${oldFile.parent}/$newName.${file.extension}"
                val newFile = file.copy(path = newPath, name = newName)
                
                // Move physical file
                oldFile.renameTo(java.io.File(newPath))
                
                // Requirement 10: Ensure no data loss during rename
                repository.renameFile(oldPath, newFile)
                
                if (_currentFile.value?.path == oldPath) {
                    _currentFile.value = newFile
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to rename file")
            }
        }
    }

    fun moveToTrash(path: String) {
        viewModelScope.launch {
            try {
                repository.moveToTrash(path)
            } catch (e: Exception) {
                Timber.e(e, "Failed to move file to trash: $path")
            }
        }
    }

    fun deleteFile(path: String) {
        viewModelScope.launch {
            try {
                repository.deleteFile(path)
            } catch (e: Exception) {
                Timber.e(e, "Failed to delete file: $path")
            }
        }
    }

    fun cacheForRecovery(content: String) {
        val file = _currentFile.value ?: return
        if (file.isReadOnly) return
        viewModelScope.launch {
            repository.saveRecoveryData(file.path, content)
        }
    }

    fun createFile(name: String, extension: String, parentDir: String) {
        viewModelScope.launch {
            val path = "$parentDir/$name.$extension"
            val newFile = TextFile(
                path = path,
                name = name,
                extension = extension,
                content = "",
                lastModified = System.currentTimeMillis()
            )
            repository.saveFile(newFile)
            openFile(path)
        }
    }

    fun toggleFavorite(file: TextFile) {
        viewModelScope.launch {
            repository.saveFile(file.copy(isFavorite = !file.isFavorite))
        }
    }

    fun setThemeMode(mode: com.example.textbook.ui.theme.ThemeMode) {
        viewModelScope.launch {
            settingsManager.setThemeMode(mode)
        }
    }

    fun restoreVersion(version: FileVersion) {
        viewModelScope.launch {
            try {
                // Requirement 5: Restoring should create a new version
                val restoredContent = repository.restoreVersion(version, applyToDisk = false)
                val file = _currentFile.value ?: return@launch
                
                saveFile(
                    restoredContent, 
                    "Restored v${version.versionNumber}", 
                    "Restored from version ${version.versionNumber} (${version.versionName})"
                )
                
                _currentFile.value = file.copy(content = restoredContent, isReadOnly = false)
                _isViewingVersion.value = false
            } catch (e: Exception) {
                Timber.e(e, "Failed to restore version")
            }
        }
    }

    fun viewVersion(version: FileVersion) {
        viewModelScope.launch {
            try {
                val versionContent = repository.restoreVersion(version, applyToDisk = false)
                val file = _currentFile.value ?: return@launch
                _isViewingVersion.value = true
                _currentFile.value = file.copy(
                    content = versionContent,
                    isReadOnly = true, // Requirement 6: Opening an old version should not allow editing
                    name = "${file.name} (v${version.versionNumber})"
                )
            } catch (e: Exception) {
                Timber.e(e, "Failed to view version")
            }
        }
    }

    private val _diffData = MutableStateFlow<Pair<String, String>?>(null)
    val diffData = _diffData.asStateFlow()

    fun showDiff(version: FileVersion) {
        viewModelScope.launch {
            val currentContent = _currentFile.value?.content ?: ""
            val versionContent = repository.restoreVersion(version, applyToDisk = false)
            _diffData.value = Pair(versionContent, currentContent)
        }
    }

    fun showDiff(version1: FileVersion, version2: FileVersion) {
        viewModelScope.launch {
            val content1 = repository.restoreVersion(version1, applyToDisk = false)
            val content2 = repository.restoreVersion(version2, applyToDisk = false)
            _diffData.value = Pair(content1, content2)
        }
    }

    // File Search (Global)
    private val _fileSearchQuery = MutableStateFlow("")
    val fileSearchQuery = _fileSearchQuery.asStateFlow()

    private val _filterType = MutableStateFlow<FilterType>(FilterType.ALL)
    val filterType = _filterType.asStateFlow()

    enum class FilterType { ALL, FAVORITES, TRASH }

    val filteredFiles = combine(allFiles, _fileSearchQuery, _filterType) { files, query, type ->
        val baseList = when(type) {
            FilterType.ALL -> files
            FilterType.FAVORITES -> files.filter { it.isFavorite }
            FilterType.TRASH -> emptyList() // Room handles trash via isDeleted
        }
        
        val finalFiles = if (type == FilterType.TRASH) {
            // Need to fetch trashFiles from repository if we want a separate Trash view
            // For now, let's keep it simple.
            repository.getTrashFiles().first()
        } else {
            baseList
        }

        if (query.isBlank()) finalFiles
        else finalFiles.filter { 
            it.name.contains(query, ignoreCase = true) || 
            it.extension.contains(query, ignoreCase = true) 
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun updateFileSearchQuery(query: String) {
        _fileSearchQuery.value = query
    }

    fun setFilterType(type: FilterType) {
        _filterType.value = type
    }

    // Search & Replace (In-file)
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    
    private val _searchResults = MutableStateFlow<List<Int>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    fun search(query: String) {
        _searchQuery.value = query
        val content = _currentFile.value?.content ?: ""
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

    fun replace(old: String, new: String) {
        val file = _currentFile.value ?: return
        val newContent = file.content.replace(old, new)
        saveFile(newContent, "Replace '$old' with '$new'")
        search(old) // Update search results after replacement
    }
}
