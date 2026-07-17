package com.example.textbook.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val rootDir = context.filesDir

    fun getFile(path: String): File = File(path)

    fun readFile(path: String): String {
        val file = File(path)
        return if (file.exists()) file.readText() else ""
    }

    fun writeFile(path: String, content: String) {
        val file = File(path)
        file.parentFile?.mkdirs()
        file.writeText(content)
    }

    fun deleteFile(path: String) {
        val file = File(path)
        if (file.exists()) file.delete()
    }

    fun getInternalPath(subDir: String, fileName: String): String {
        val dir = File(rootDir, subDir)
        if (!dir.exists()) dir.mkdirs()
        return File(dir, fileName).absolutePath
    }
}
