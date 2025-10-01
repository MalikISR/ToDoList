package com.example.domain.usecase

class NoteUseCase(
    val getNotes: GetNotesUseCase,
    val addNote: AddNoteUseCase,
    val deleteNote: DeleteNoteUseCase
)