package com.example.textbook

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class TextBookApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        // Requirement 2: Initialize Syntax Highlighter from keywords file
        try {
            val keywords = assets.open("kotlin_keywords.txt").bufferedReader().useLines { lines ->
                lines.filter { it.isNotBlank() }.toSet()
            }
            com.example.textbook.editor.SyntaxHighlighter.initialize(keywords)
        } catch (e: Exception) {
            Timber.e(e, "Failed to load keywords from assets")
        }
    }
}
