package com.example.textbook.core.di

import android.content.Context
import androidx.room.Room
import com.example.textbook.data.AppDatabase
import com.example.textbook.data.FileDao
import com.example.textbook.data.TextBookRepositoryImpl
import com.example.textbook.domain.TextBookRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindRepository(impl: TextBookRepositoryImpl): TextBookRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "textbook_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideFileDao(database: AppDatabase): FileDao {
        return database.fileDao()
    }
}
