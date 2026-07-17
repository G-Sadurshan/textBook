package com.example.textbook.data

import com.example.textbook.domain.FileVersion
import com.example.textbook.domain.TextFile
import com.example.textbook.domain.TextBookRepository
import com.example.textbook.vcs.DiffManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextBookRepositoryImpl @Inject constructor(
    private val fileDao: FileDao,
    private val storageManager: StorageManager,
    private val diffManager: DiffManager
) : TextBookRepository {

    override fun getAllFiles(): Flow<List<TextFile>> = fileDao.getAllFiles().map { list ->
        list.map { it.toDomain(storageManager.readFile(it.path)) }
    }

    override fun getFavoriteFiles(): Flow<List<TextFile>> = fileDao.getFavoriteFiles().map { list ->
        list.map { it.toDomain(storageManager.readFile(it.path)) }
    }

    override fun getPinnedFiles(): Flow<List<TextFile>> = fileDao.getPinnedFiles().map { list ->
        list.map { it.toDomain(storageManager.readFile(it.path)) }
    }

    override fun getRecentFiles(): Flow<List<TextFile>> = fileDao.getRecentFiles().map { list ->
        list.map { it.toDomain(storageManager.readFile(it.path)) }
    }

    override fun getTrashFiles(): Flow<List<TextFile>> = fileDao.getTrashFiles().map { list ->
        list.map { it.toDomain(storageManager.readFile(it.path)) }
    }

    override suspend fun getFileByPath(path: String): TextFile? {
        val entity = fileDao.getFileByPath(path) ?: return null
        val content = storageManager.readFile(path)
        fileDao.insertRecentFile(RecentFileEntity(path, System.currentTimeMillis()))
        return entity.toDomain(content)
    }

    override suspend fun saveFile(file: TextFile) {
        val entity = FileEntity(
            path = file.path,
            name = file.name,
            extension = file.extension,
            lastModified = System.currentTimeMillis(),
            isReadOnly = file.isReadOnly,
            isPinned = file.isPinned,
            isFavorite = file.isFavorite
        )
        fileDao.insertFile(entity)
        storageManager.writeFile(file.path, file.content)
    }

    override suspend fun deleteFile(path: String) {
        val entity = fileDao.getFileByPath(path)
        if (entity != null) {
            fileDao.deleteFile(entity)
            storageManager.deleteFile(path)
        }
    }

    override suspend fun moveToTrash(path: String) {
        fileDao.moveToTrash(path)
    }

    override suspend fun restoreFromTrash(path: String) {
        fileDao.restoreFromTrash(path)
    }

    override fun getVersionsForFile(path: String): Flow<List<FileVersion>> = 
        fileDao.getVersionsForFile(path).map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun createVersion(path: String, name: String, comment: String?, content: String) {
        val currentContent = storageManager.readFile(path)
        val versions = fileDao.getVersionsForFile(path).first()
        val nextNumber = (versions.maxByOrNull { it.versionNumber }?.versionNumber ?: 0) + 1
        
        // Store how to get from the NEW content back to the OLD content
        val diff = diffManager.generateDiffJson(content, currentContent)
        val version = VersionEntity(
            filePath = path,
            versionName = name,
            versionNumber = nextNumber,
            timestamp = System.currentTimeMillis(),
            comment = comment,
            diffContent = diff
        )
        fileDao.insertVersion(version)
    }

    override suspend fun restoreVersion(version: FileVersion, applyToDisk: Boolean): String {
        val currentContent = storageManager.readFile(version.filePath)
        val restoredContent = diffManager.applyDiffJson(currentContent, version.diffContent)
        if (applyToDisk) {
            storageManager.writeFile(version.filePath, restoredContent)
        }
        return restoredContent
    }

    override suspend fun getRecoveryData(path: String): String? {
        return fileDao.getRecoveryData(path)?.tempContent
    }

    override suspend fun saveRecoveryData(path: String, content: String) {
        fileDao.saveRecoveryData(CrashRecoveryEntity(path, content, System.currentTimeMillis()))
    }

    override suspend fun clearRecoveryData(path: String) {
        fileDao.clearRecoveryData(path)
    }

    private fun FileEntity.toDomain(content: String) = TextFile(
        path = path,
        name = name,
        extension = extension,
        content = content,
        lastModified = lastModified,
        isReadOnly = isReadOnly,
        isPinned = isPinned,
        isFavorite = isFavorite
    )

    private fun VersionEntity.toDomain() = FileVersion(
        id = id,
        filePath = filePath,
        versionName = versionName,
        versionNumber = versionNumber,
        timestamp = timestamp,
        comment = comment,
        diffContent = diffContent,
        isFavorite = isFavorite
    )
}
