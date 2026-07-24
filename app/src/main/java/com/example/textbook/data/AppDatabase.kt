package com.example.textbook.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FileDao {
    @Query("SELECT * FROM files WHERE isDeleted = 0")
    fun getAllFiles(): Flow<List<FileEntity>>

    @Query("SELECT * FROM files WHERE isFavorite = 1 AND isDeleted = 0")
    fun getFavoriteFiles(): Flow<List<FileEntity>>

    @Query("SELECT * FROM files WHERE isPinned = 1 AND isDeleted = 0")
    fun getPinnedFiles(): Flow<List<FileEntity>>

    @Query("SELECT * FROM files WHERE isDeleted = 1")
    fun getTrashFiles(): Flow<List<FileEntity>>

    @Query("SELECT * FROM files WHERE path = :path")
    suspend fun getFileByPath(path: String): FileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: FileEntity)

    @Update
    suspend fun updateFile(file: FileEntity)

    @Delete
    suspend fun deleteFile(file: FileEntity)

    @Query("UPDATE files SET isDeleted = 1 WHERE path = :path")
    suspend fun moveToTrash(path: String)

    @Query("UPDATE files SET isDeleted = 0 WHERE path = :path")
    suspend fun restoreFromTrash(path: String)

    @Query("UPDATE versions SET filePath = :newPath WHERE filePath = :oldPath")
    suspend fun updateVersionPaths(oldPath: String, newPath: String)

    // Versioning
    @Query("SELECT * FROM versions WHERE filePath = :path ORDER BY timestamp DESC")
    fun getVersionsForFile(path: String): Flow<List<VersionEntity>>

    @Query("SELECT * FROM versions WHERE filePath = :path ORDER BY timestamp DESC")
    suspend fun getVersionsForFileSync(path: String): List<VersionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVersion(version: VersionEntity)

    @Query("DELETE FROM versions WHERE id = :versionId")
    suspend fun deleteVersion(versionId: Long)

    // Recent Files
    @Query("""
        SELECT f.* FROM files f 
        INNER JOIN recent_files r ON f.path = r.filePath 
        WHERE f.isDeleted = 0 
        ORDER BY r.lastOpened DESC 
        LIMIT 20
    """)
    fun getRecentFiles(): Flow<List<FileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentFile(recent: RecentFileEntity)

    // Crash Recovery
    @Query("SELECT * FROM crash_recovery WHERE filePath = :path")
    suspend fun getRecoveryData(path: String): CrashRecoveryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRecoveryData(data: CrashRecoveryEntity)

    @Query("DELETE FROM crash_recovery WHERE filePath = :path")
    suspend fun clearRecoveryData(path: String)
}

@Database(
    entities = [
        FileEntity::class, 
        VersionEntity::class, 
        RecentFileEntity::class, 
        CrashRecoveryEntity::class
    ], 
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fileDao(): FileDao
}
