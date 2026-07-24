package com.example.textbook.domain

import kotlinx.coroutines.flow.Flow

interface TextBookRepository {
    fun getAllFiles(): Flow<List<TextFile>>
    fun getFavoriteFiles(): Flow<List<TextFile>>
    fun getPinnedFiles(): Flow<List<TextFile>>
    fun getRecentFiles(): Flow<List<TextFile>>
    fun getTrashFiles(): Flow<List<TextFile>>
    
    suspend fun getFileByPath(path: String): TextFile?
    suspend fun saveFile(file: TextFile)
    suspend fun deleteFile(path: String)
    suspend fun moveToTrash(path: String)
    suspend fun restoreFromTrash(path: String)
    suspend fun renameFile(oldPath: String, newFile: TextFile)
    
    // Versions
    fun getVersionsForFile(path: String): Flow<List<FileVersion>>
    suspend fun createVersion(path: String, name: String, comment: String?, content: String)
    suspend fun restoreVersion(version: FileVersion, applyToDisk: Boolean = true): String
    
    // Recovery
    suspend fun getRecoveryData(path: String): String?
    suspend fun saveRecoveryData(path: String, content: String)
    suspend fun clearRecoveryData(path: String)
}
