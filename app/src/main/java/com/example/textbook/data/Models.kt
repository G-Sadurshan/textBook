package com.example.textbook.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "files")
data class FileEntity(
    @PrimaryKey val path: String,
    val name: String,
    val extension: String,
    val lastModified: Long,
    val isReadOnly: Boolean = false,
    val isPinned: Boolean = false,
    val isFavorite: Boolean = false,
    val isDeleted: Boolean = false // For Trash feature
)

@Entity(
    tableName = "versions",
    foreignKeys = [
        ForeignKey(
            entity = FileEntity::class,
            parentColumns = ["path"],
            childColumns = ["filePath"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["filePath"])]
)
data class VersionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val filePath: String,
    val versionName: String,
    val timestamp: Long,
    val comment: String?,
    val diffContent: String, // Storing incremental changes (deltas)
    val isFavorite: Boolean = false
)

@Entity(
    tableName = "recent_files",
    foreignKeys = [
        ForeignKey(
            entity = FileEntity::class,
            parentColumns = ["path"],
            childColumns = ["filePath"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["filePath"])]
)
data class RecentFileEntity(
    @PrimaryKey val filePath: String,
    val lastOpened: Long
)

@Entity(tableName = "crash_recovery")
data class CrashRecoveryEntity(
    @PrimaryKey val filePath: String,
    val tempContent: String,
    val timestamp: Long
)
