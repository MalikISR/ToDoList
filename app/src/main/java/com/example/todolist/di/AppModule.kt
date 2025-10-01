package com.example.todolist.di

import android.content.Context
import androidx.room.Room
import com.example.data.local.NoteDao
import com.example.data.local.NoteDatabase
import com.example.data.repository.LocalNoteRepositoryImpl
import com.example.domain.repository.NoteRepository
import com.example.domain.usecase.*
import com.example.todolist.repository.FirebaseNoteDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideRepository(noteDao: NoteDao): NoteRepository {
        return LocalNoteRepositoryImpl(noteDao)
    }

    @Provides
    fun provideUseCases(repository: NoteRepository): NoteUseCase {
        return NoteUseCase(
            getNotes = GetNotesUseCase(repository),
            addNote = AddNoteUseCase(repository),
            deleteNote = DeleteNoteUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NoteDatabase {
        return Room.databaseBuilder(
            context,
            NoteDatabase::class.java,
            "note_db"
        ).build()
    }

    @Provides
    fun provideNoteDao(database: NoteDatabase): NoteDao {
        return database.noteDao()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseNoteDataSource(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): FirebaseNoteDataSource = FirebaseNoteDataSource(firestore, auth)

}
