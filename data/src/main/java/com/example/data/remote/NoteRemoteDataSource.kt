package com.example.data.remote

import com.example.domain.model.Note

interface NoteRemoteDataSource {
    fun isAuthorized(): Boolean
    suspend fun saveNoteRemote(note: Note)
    suspend fun getNotesRemote(): List<NoteRemote>
    suspend fun deleteNoteRemote(noteId: String)
}