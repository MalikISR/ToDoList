package com.example.domain.usecase.note

import com.example.domain.model.Note
import com.example.domain.repository.NoteRepository
import javax.inject.Inject

class TogglePinUseCase @Inject constructor(
    private val repo: NoteRepository
) {
    suspend operator fun invoke(note: Note) {
        val updated = note.copy(isPinned = !note.isPinned)
        repo.updateNote(updated)
    }
}
