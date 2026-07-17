package com.example.textbook.domain

data class TextFile(
    val path: String,
    val name: String,
    val extension: String,
    val content: String,
    val lastModified: Long,
    val isReadOnly: Boolean = false,
    val isPinned: Boolean = false,
    val isFavorite: Boolean = false
)

data class FileVersion(
    val id: Long,
    val filePath: String,
    val versionName: String,
    val timestamp: Long,
    val comment: String?,
    val diffContent: String,
    val isFavorite: Boolean = false
)
