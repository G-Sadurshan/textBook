# TextBook - Modern Mobile Text Editor

TextBook is a production-quality mobile text editor for Android, designed for developers, students, and technical writers. It features incremental version control, professional syntax highlighting, and a modern Material 3 interface.

## Key Features

- **Rich Text Editing**: Monospace font, line numbers, word wrap, and zoom support.
- **Incremental Version Control**: Uses `java-diff-utils` to store only differences between versions, saving storage while providing full history.
- **Syntax Highlighting**: Keyword-based highlighting for Kotlin, Java, JavaScript, JSON, XML, and Markdown.
- **Markdown Preview**: Real-time rendering of Markdown documents including headings, lists, and code blocks.
- **Advanced Search**: Global search and replace within files.
- **Crash Recovery**: Automatic caching of content to prevent data loss in case of unexpected closures.
- **Modern UI**: Material 3 design with dynamic colors, smooth animations, and a premium "glassmorphism" feel.
- **Offline First**: Everything works locally on internal storage.

## Tech Stack

- **Language**: 100% Kotlin
- **UI**: Jetpack Compose with Material 3
- **Architecture**: MVVM + Repository Pattern (Clean Architecture)
- **Dependency Injection**: Hilt
- **Database**: Room Database
- **Versioning**: java-diff-utils (Incremental storage)
- **Preferences**: DataStore
- **Logging**: Timber
- **Markdown**: commonmark-java

## Project Structure

- `app`: Main application module.
- `core`: Shared utilities, DI modules, and settings management.
- `data`: Room entities, DAOs, Repository implementation, and Storage Manager.
- `domain`: Core business models and Repository interface.
- `ui`: Jetpack Compose screens, ViewModels, and Navigation.
- `editor`: Syntax highlighting logic and Undo/Redo management.
- `vcs`: Diff management and versioning logic.

## Build Requirements

- Android Studio Koala+
- JDK 17
- Android SDK 35+
