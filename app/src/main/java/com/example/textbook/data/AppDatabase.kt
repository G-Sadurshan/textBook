package com.example.textbook.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FileDao {
    @Query("SELECT * FROM files")
    fun getAllFiles(): Flow<List<FileEntity>>

    @Query("SELECT * FROM files WHERE path = :path")
    suspend fun getFileByPath(path: String): FileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: FileEntity)

    @Delete
    suspend fun deleteFile(file: FileEntity)

    // Versioning
    @Query("SELECT * FROM versions WHERE filePath = :path ORDER BY timestamp DESC")
    fun getVersionsForFile(path: String): Flow<List<VersionEntity>>

    @Insert
    suspend fun insertVersion(version: VersionEntity)

    // Crash Recovery
    @Query("SELECT * FROM crash_recovery WHERE filePath = :path")
    suspend fun getRecoveryData(path: String): CrashRecoveryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRecoveryData(data: CrashRecoveryEntity)

    @Query("DELETE FROM crash_recovery WHERE filePath = :path")
    suspend fun clearRecoveryData(path: String)
}

@Database(entities = [FileEntity::class, VersionEntity::class, CrashRecoveryEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fileDao(): FileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "textbook_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
