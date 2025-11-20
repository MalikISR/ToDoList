package com.example.domain.repository

import com.example.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    suspend fun insertNote(note: Note)
    suspend fun deleteNote(note: Note)
    suspend fun getNoteById(id: Int): Note?
    suspend fun updateNote(note: Note)
    suspend fun sync()
}