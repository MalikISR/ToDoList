package com.example.domain.usecase

import com.example.domain.repository.NoteRepository
import javax.inject.Inject

class SyncNotesFromServerUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke() = repository.syncFromServer()
}
