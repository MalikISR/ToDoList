package com.example.todolist.presentation.note

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Note
import com.example.domain.usecase.auth.IsAuthorizedUseCase
import com.example.domain.usecase.note.AddNoteUseCase
import com.example.domain.usecase.note.DeleteNoteUseCase
import com.example.domain.usecase.note.GetNotesUseCase
import com.example.domain.usecase.note.SyncNotesUseCase
import com.example.domain.usecase.note.ToggleDoneUseCase
import com.example.domain.usecase.note.TogglePinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    getNotesUseCase: GetNotesUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val syncNotesUseCase: SyncNotesUseCase,
    private val togglePinUseCase: TogglePinUseCase,
    private val toggleDoneUseCase: ToggleDoneUseCase,
    private val isAuthorizedUseCase: IsAuthorizedUseCase,
    private val addNoteUseCase: AddNoteUseCase,
) : ViewModel() {

    val notes: StateFlow<List<Note>> =
        getNotesUseCase()
            .stateIn(
                viewModelScope,
                kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    private val _selectedNotes = MutableStateFlow(setOf<String>())
    val selectedNotes: StateFlow<Set<String>> = _selectedNotes.asStateFlow()

    private val _syncing = MutableStateFlow(false)
    val syncing: StateFlow<Boolean> = _syncing.asStateFlow()

    init {
        syncServer()
    }

    fun isAuthorized(): Boolean = isAuthorizedUseCase()

    private fun deleteNote(note: Note) {
        viewModelScope.launch {
            deleteNoteUseCase(note)
        }
    }

    fun toggleDone(note: Note) {
        viewModelScope.launch {
            toggleDoneUseCase(note)
        }
    }

    fun syncServer() {
        if (_syncing.value) return
        if (!isAuthorized()) return
        _syncing.value = true
        viewModelScope.launch {
            try {
                syncNotesUseCase()
            } finally {
                _syncing.value = false
            }
        }
    }

    fun createDraftNote(): String {
        val id = UUID.randomUUID().toString()

        val note = Note(
            id = id,
            title = "",
            description = "",
            color = Color.Green.toArgb(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            deadline = System.currentTimeMillis() + 86400000,
            isDeleted = false,
            isPinned = false,
            isDone = false
        )

        viewModelScope.launch {
            addNoteUseCase(note)
        }
        return id
    }

    fun toggleSelection(noteId: String) {
        val current = _selectedNotes.value
        _selectedNotes.value = if (current.contains(noteId)) {
            current - noteId
        } else {
            current + noteId
        }
    }

    fun clearSelection() {
        _selectedNotes.value = emptySet()
    }

    fun deleteSelected() {
        viewModelScope.launch {
            val notesToDelete = notes.value.filter { it.id in _selectedNotes.value }
            notesToDelete.forEach { deleteNote(it) }
            clearSelection()
        }
    }

    fun pinSelected() {
        viewModelScope.launch {
            val selected = notes.value.filter { it.id in _selectedNotes.value }
            selected.forEach { togglePinUseCase(it) }
            clearSelection()
        }
    }

    fun filteredNotes(
        notes: List<Note>,
        searchQuery: String,
        selectedFilters: Set<NoteColorFilter>
    ): List<Note> {
        return notes.filter { note ->
            note.title.contains(searchQuery, true) ||
                    note.description.contains(searchQuery, true)
        }
            .filter { note ->
                selectedFilters.isEmpty() || selectedFilters.contains(
                    when (note.color) {
                        Color.Red.toArgb() -> NoteColorFilter.Red
                        Color.Yellow.toArgb() -> NoteColorFilter.Yellow
                        Color.Green.toArgb() -> NoteColorFilter.Green
                        else -> return@filter true
                    }
                )
            }
    }

    fun sortNotes(notes: List<Note>, sort: NoteSort): List<Note> {
        return notes.sortedWith(
            compareBy<Note> { pinWeight(it) }
                .thenBy { doneWeight(it) }
                .then(
                    when (sort) {

                        NoteSort.ByDateAsc ->
                            compareBy { it.createdAt }

                        NoteSort.ByDateDesc ->
                            compareByDescending { it.createdAt }

                        NoteSort.ByDeadlineAsc ->
                            compareBy<Note> { deadlineGroup(it.deadline) }
                                .thenBy { deadlineSort(it) }

                        NoteSort.ByDeadlineDesc ->
                            compareBy<Note> { deadlineGroup(it.deadline) }
                                .thenByDescending { deadlineSort(it) }

                        NoteSort.ByTitleAsc ->
                            compareBy { it.title.lowercase() }

                        NoteSort.ByTitleDesc ->
                            compareByDescending { it.title.lowercase() }

                        NoteSort.ByPriority ->
                            compareBy { priorityWeight(it.color) }
                    }
                )
        )
    }

    private fun priorityWeight(color: Int): Int {
        return when (color) {
            Color.Red.toArgb() -> 0
            Color.Yellow.toArgb() -> 1
            Color.Green.toArgb() -> 2
            else -> 2
        }
    }

    private fun pinWeight(note: Note): Int {
        return if (note.isPinned) 0 else 1
    }

    private fun doneWeight(note: Note): Int {
        return if (note.isDone) 2 else 1
    }

    private fun deadlineGroup(deadline: Long?): Int {
        if (deadline == null) return 1

        val now = System.currentTimeMillis()

        return when {
            deadline >= now -> 0
            else -> 2
        }
    }

    private fun deadlineSort(note: Note): Long {
        val deadline = note.deadline
        val now = System.currentTimeMillis()

        return when {
            deadline >= now -> deadline
            else -> Long.MAX_VALUE - deadline
        }
    }
}

