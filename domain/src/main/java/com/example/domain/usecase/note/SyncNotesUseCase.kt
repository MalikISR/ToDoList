package com.example.domain.usecase.note

import com.example.domain.repository.NoteRepository
import com.example.domain.usecase.auth.IsAuthorizedUseCase
import javax.inject.Inject

class SyncNotesUseCase @Inject constructor(
    private val repo: NoteRepository,
    private val isAuthorizedUseCase: IsAuthorizedUseCase
) {
    suspend operator fun invoke() {
        if (!isAuthorizedUseCase()) return
        repo.sync()
    }
}

