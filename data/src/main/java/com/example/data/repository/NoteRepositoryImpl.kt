package com.example.data.repository

import com.example.data.local.NoteDao
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.domain.model.Note
import com.example.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalNoteRepositoryImpl(private val noteDao: NoteDao): NoteRepository {

    override fun getAllNotes(): Flow<List<Note>> {
        return noteDao.getAllNotes().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun insertNote(note: Note) {
        noteDao.insertNote(note.toEntity())
    }

    override suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note.toEntity())
    }

    override suspend fun getNoteByIdUseCase(id: Int): Note {
        return noteDao.getNoteById(id).toDomain()
    }

    override suspend fun updateNoteUseCase(note: Note) {
        noteDao.updateNote(note.toEntity())
    }

    override suspend fun syncNotes() {
        TODO("Not yet implemented")
    }

}