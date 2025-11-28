package com.example.domain.usecase.note

import com.example.domain.model.Note
import com.example.domain.repository.NoteRepository
import javax.inject.Inject

class ToggleDoneUseCase @Inject constructor(
    private val repo: NoteRepository
) {
    suspend operator fun invoke(note: Note) {
        val updated = note.copy(isDone = !note.isDone, updatedAt = System.currentTimeMillis())
        repo.updateNote(updated)
    }
}
