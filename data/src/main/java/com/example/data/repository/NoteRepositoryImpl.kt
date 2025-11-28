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
        local.getAllNotes()
            .map { list ->
                list
                    .map { it.toDomain() }
                    .filter { !it.isDeleted }
            }

    override suspend fun insertNote(note: Note) {
        local.insertNote(note.toEntity())
    }

    override suspend fun updateNote(note: Note) {
        local.updateNote(note.toEntity())
    }

    override suspend fun deleteNote(note: Note) {
        val entity = note.copy(isDeleted = true, isSynced = false)
        local.updateNote(entity.toEntity())
    }

    override suspend fun getNoteById(id: String): Note? {
        return local.getNoteById(id)?.toDomain()?.takeIf { !it.isDeleted }
    }

    override suspend fun sync() {

        // 0. Проверка авторизации
        if (!remote.isAuthorized()) {
            return
        }
        // === 1. Получаем серверные заметки ===
        val remoteNotes = remote.getNotesRemote().map { it.toDomain() }
        val remoteIds = remoteNotes.map { it.id }.toSet()
        val localNotesOnce = local.getAllNotesOnce()


        // === 2. Получаем локальные заметки ===
        val localNotes = localNotesOnce

        // === 3. Обновляем локальные заметки на основе сервера ===
        remoteNotes.forEach { remoteNote ->
            val localNote = localNotes.find { it.id == remoteNote.id }

            when {
                // на устройстве нет → добавляем
                localNote == null -> {
                    local.insertNote(
                        remoteNote.copy(isSynced = true).toEntity()
                    )
                }

                // сервер новее → обновляем локальную
                remoteNote.updatedAt > localNote.updatedAt -> {
                    local.updateNote(
                        remoteNote.copy(isSynced = true).toEntity()
                    )
                }
            }
        }

        // === 4. Определяем локальные изменения, которые нужно отправить ===
        val pendingLocal = localNotesOnce.filter { !it.isSynced } // все, кто требует отправки

        pendingLocal.forEach { localNote ->
            if (localNote.isDeleted) {
                remote.deleteNoteRemote(localNote.id)
                local.deleteNote(localNote)
            } else {
                remote.saveNoteRemote(localNote.toDomain())
                local.updateNote(
                    localNote.copy(isSynced = true)
                )
            }
        }
    }
}

