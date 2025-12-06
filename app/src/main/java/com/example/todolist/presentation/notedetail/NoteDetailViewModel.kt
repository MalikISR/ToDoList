package com.example.todolist.presentation.notedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Note
import com.example.domain.usecase.note.GetNoteByIdUseCase
import com.example.domain.usecase.note.UpdateNoteUseCase
import com.example.todolist.notification.NoteDeadlineScheduler
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
    private val noteDeadlineScheduler: NoteDeadlineScheduler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _note = MutableStateFlow<Note?>(null)
    val note: StateFlow<Note?> = _note.asStateFlow()

    private val _savedEvent = MutableStateFlow(false)
    val savedEvent: StateFlow<Boolean> = _savedEvent

    private val noteChanges = MutableStateFlow<Note?>(null)
    private var lastSavedNote: Note? = null

    init {
        val noteId = savedStateHandle.get<String>("noteId")

        viewModelScope.launch {
            _note.value = noteId?.let { getNoteById(it) }
        }

        viewModelScope.launch {
            noteChanges
                .filterNotNull()
                .debounce(1200)
                .filter { hasChanges(it) }
                .collect { latest ->
                    updateNote(latest)
                    lastSavedNote = latest

                    val deadline = latest.deadline
                    noteDeadlineScheduler.schedule(
                        noteId = latest.id,
                        noteTitle = latest.title,
                        deadlineMillis = deadline
                    )
                    flashSaveIndicator()
                }
        }
    }

    fun saveBeforeLeave() {
        val current = noteChanges.value ?: return
        viewModelScope.launch {
            updateNote(current)
            lastSavedNote = current

            val deadline = current.deadline
            noteDeadlineScheduler.schedule(
                noteId = current.id,
                noteTitle = current.title,
                deadlineMillis = deadline
            )
            flashSaveIndicator()
        }
    }

    fun onNoteChanged(note: Note) {
        noteChanges.value = note.copy(
            updatedAt = System.currentTimeMillis(),
            isSynced = false
        )
    }

    private fun hasChanges(new: Note): Boolean {
        return new != lastSavedNote
    }

    private suspend fun flashSaveIndicator() {
        _savedEvent.value = true
        delay(1200)
        _savedEvent.value = false
    }
}
