package com.example.data.remote

import com.example.domain.model.Note

interface NoteRemoteDataSource {
    suspend fun saveNoteRemote(note: Note)
    suspend fun getNotesRemote(): List<NoteRemote>
    suspend fun deleteNoteRemote(noteId: Int)
}