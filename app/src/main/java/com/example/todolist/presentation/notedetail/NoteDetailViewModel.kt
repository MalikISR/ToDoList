package com.example.todolist.presentation.notedetail

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Note
import com.example.domain.usecase.AddNoteUseCase
import com.example.domain.usecase.GetNoteByIdUseCase
import com.example.domain.usecase.UpdateNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val getNoteById: GetNoteByIdUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    private val updateNote: UpdateNoteUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _note = MutableStateFlow<Note?>(null)
    val note: StateFlow<Note?> = _note

    init {
        val noteId = savedStateHandle.get<Int>("noteId")
        if (noteId != null){
            viewModelScope.launch {
                _note.value = getNoteById(noteId)
            }
        } else {
            _note.value = Note(
                id = 0,
                title = "",
                description = "",
                color = Color.Green.toArgb(),
                timestamp = System.currentTimeMillis(),
                isPinned = false
            )
        }
    }

    fun saveNote(note: Note){
        viewModelScope.launch {
            if (note.id == 0) addNoteUseCase(note)
            else updateNote(note)
        }
    }
}