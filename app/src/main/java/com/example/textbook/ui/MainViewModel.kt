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

    val allFiles = repository.getAllFiles()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val recentFiles = repository.getRecentFiles()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val favoriteFiles = repository.getFavoriteFiles()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        
    val pinnedFiles = repository.getPinnedFiles()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val versions = _currentFile.flatMapLatest { file ->
        if (file != null) repository.getVersionsForFile(file.path)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

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
        val file = _currentFile.value ?: return
        viewModelScope.launch {
            val updated = file.copy(isReadOnly = !file.isReadOnly)
            repository.saveFile(updated)
            _currentFile.value = updated
        }
    }

    fun saveFile(content: String, versionName: String? = null, comment: String? = null) {
        val file = _currentFile.value ?: return
        if (file.isReadOnly) return
        
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
                val restoredContent = repository.restoreVersion(version)
                val file = _currentFile.value ?: return@launch
                _currentFile.value = file.copy(content = restoredContent)
            } catch (e: Exception) {
                Timber.e(e, "Failed to restore version")
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
