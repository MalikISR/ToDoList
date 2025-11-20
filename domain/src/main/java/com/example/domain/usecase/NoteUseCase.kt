package com.example.domain.usecase

class NoteUseCase(
    val getNotes: GetNotesUseCase,
    val addNote: AddNoteUseCase,
    val deleteNote: DeleteNoteUseCase,
    val updateNote: UpdateNoteUseCase,
    val syncFromServer: SyncNotesUseCase,
)