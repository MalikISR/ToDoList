package com.example.todolist.presentation.notedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Note
import com.example.domain.usecase.note.GetNoteByIdUseCase
import com.example.domain.usecase.note.UpdateNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val getNoteById: GetNoteByIdUseCase,
    private val updateNote: UpdateNoteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _note = MutableStateFlow<Note?>(null)
    val note: StateFlow<Note?> = _note.asStateFlow()

    private val _savedEvent = MutableStateFlow(false)
    val savedEvent: StateFlow<Boolean> = _savedEvent

    private val autoSaveFlow = MutableStateFlow<Note?>(null)

    init {
        val noteId = savedStateHandle.get<String>("noteId")

        viewModelScope.launch {
            _note.value = noteId?.let { getNoteById(it) }
        }

        viewModelScope.launch {
            autoSaveFlow
                .filterNotNull()
                .debounce(5000)
                .collect { note ->
                    updateNote(note)
                }
        }
    }

    fun saveNote(note: Note) {
        viewModelScope.launch {
            updateNote(note)
            showSavedIndicator()
        }
    }

    fun onNoteChanged(note: Note) {
        autoSaveFlow.value = note.copy(
            updatedAt = System.currentTimeMillis(),
            isSynced = false
        )
    }

    private suspend fun showSavedIndicator() {
        _savedEvent.value = true
        delay(1500)
        _savedEvent.value = false
    }
}
