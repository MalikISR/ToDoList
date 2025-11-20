package com.example.data.repository

import com.example.data.local.NoteDao
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.data.remote.NoteRemoteDataSource
import com.example.data.remote.toDomain
import com.example.domain.model.Note
import com.example.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepositoryImpl(
    private val local: NoteDao,
    private val remote: NoteRemoteDataSource
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

    override suspend fun sync() {
        // 1. Загружаем серверные заметки
        val remoteNotes = remote.getNotesRemote().map { it.toDomain() }
        val remoteIds = remoteNotes.map { it.id }.toSet()

        // 2. Берём локальные (один раз для эффективности)
        val localNotes = local.getAllNotesOnce()

        // 3. Обновляем локальные на основании сервера
        remoteNotes.forEach { remoteNote ->
            val localNote = localNotes.find { it.id == remoteNote.id }

            when {
                localNote == null -> {
                    // На устройстве нет — добавляем
                    local.insertNote(
                        remoteNote.toEntity().copy(isSynced = true)
                    )
                }

                // Сервер новее → обновляем локально
                remoteNote.updatedAt > localNote.updatedAt -> {
                    local.updateNote(
                        remoteNote.toEntity().copy(isSynced = true)
                    )
                }
            }
        }

        // 4. Удаляем локально то, что удалено на сервере
        localNotes
            .filter { it.id !in remoteIds }
            .forEach { local.deleteNote(it) }

        // 5. Ищем локальные заметки, которые изменились позже сервера
        val freshLocal = local.getAllNotesOnce()
            .filter { !it.isDeleted && !it.isSynced }

        freshLocal.forEach { localNote ->
            val remoteNote = remoteNotes.find { it.id == localNote.id }

            when {
                // На сервере нет → отправляем
                remoteNote == null -> {
                    remote.saveNoteRemote(localNote.toDomain())
                    local.updateNote(localNote.copy(isSynced = true))
                }

                // Локальная новее → отправляем обновление
                localNote.updatedAt > remoteNote.updatedAt -> {
                    remote.saveNoteRemote(localNote.toDomain())
                    local.updateNote(localNote.copy(isSynced = true))
                }
            }
        }
    }

}
