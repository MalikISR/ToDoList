package com.example.todolist.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.example.data.local.NoteDao
import com.example.data.local.NoteDatabase
import com.example.data.migration.MIGRATION_1_2
import com.example.todolist.notification.NoteDeadlineScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NoteDatabase {
        return Room.databaseBuilder(
            context,
            NoteDatabase::class.java,
            "note_db"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideNoteDao(database: NoteDatabase): NoteDao {
        return database.noteDao()
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)

    @Provides
    @Singleton
    fun provideNoteDeadlineScheduler(workManager: WorkManager): NoteDeadlineScheduler =
        NoteDeadlineScheduler(workManager)
}
