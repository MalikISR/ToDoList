package com.example.todolist.presentation.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Note
import com.example.domain.usecase.DeleteNoteUseCase
import com.example.domain.usecase.GetNotesUseCase
import com.example.domain.usecase.SyncNotesFromServerUseCase
import com.example.domain.usecase.SyncNotesToServerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val syncFromServerUseCase: SyncNotesFromServerUseCase,
    private val syncToServerUseCase: SyncNotesToServerUseCase
) : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    init {
        loadNotes()
    }

    fun loadNotes() {
        viewModelScope.launch {
            getNotesUseCase().collect { result ->
                _notes.value = result
            }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            deleteNoteUseCase(note)
            loadNotes()
        }
    }

    fun syncFromServer() {
        viewModelScope.launch {
            syncFromServerUseCase()
            loadNotes() // после загрузки обновляем UI
        }
    }

    fun syncToServer() {
        viewModelScope.launch {
            syncToServerUseCase()
        }
    }
}
