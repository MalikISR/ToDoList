package com.example.data.repository

import com.example.data.local.NoteDao
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.data.remote.FirebaseNoteDataSource
import com.example.data.remote.toDomain
import com.example.domain.model.Note
import com.example.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepositoryImpl(
    private val local: NoteDao,
    private val remote: FirebaseNoteDataSource
) : NoteRepository {

    override fun getAllNotes(): Flow<List<Note>> =
        local.getAllNotes().map { list -> list.map { it.toDomain() } }

    override suspend fun insertNote(note: Note) {
        local.insertNote(note.toEntity())
        remote.saveNoteRemote(note)
    }

    override suspend fun updateNote(note: Note) {
        local.updateNote(note.toEntity())
        remote.saveNoteRemote(note)
    }

    override suspend fun deleteNote(note: Note) {
        local.deleteNote(note.toEntity())
        remote.deleteNoteRemote(note.id)
    }

    override suspend fun getNoteById(id: Int): Note? {
        return local.getNoteById(id)?.toDomain()
    }

    override suspend fun syncFromServer() {
        val remoteNotes = remote.getNotesRemote().map { it.toDomain() }
        remoteNotes.forEach { note ->
            local.insertNote(note.toEntity().copy(isSynced = true))
        }
    }

    override suspend fun syncToServer() {
        val localNotes = local.getAllNotesOnce().filter { !it.isDeleted && !it.isSynced }
        localNotes.forEach { noteEntity ->
            remote.saveNoteRemote(noteEntity.toDomain())
            local.updateNote(noteEntity.copy(isSynced = true))
        }
    }
}
