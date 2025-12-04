package com.example.todolist.presentation.note

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todolist.R
import com.example.todolist.presentation.profile.ProfileViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch

enum class NoteColorFilter { Red, Yellow, Green }

enum class NoteSort {
    ByDateDesc,
    ByDateAsc,
    ByDeadlineAsc,
    ByDeadlineDesc,
    ByTitleAsc,
    ByTitleDesc,
    ByPriority
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(
    viewModel: NoteViewModel = hiltViewModel(),
    onNoteClick: (String) -> Unit
) {
    val notes by viewModel.notes.collectAsState()

    val profileViewModel: ProfileViewModel = hiltViewModel()
    val shouldSync by profileViewModel.shouldSync.collectAsState()

    var noteToDelete by remember { mutableStateOf(false) }

    val syncing by viewModel.syncing.collectAsState()
    val state = rememberSwipeRefreshState(isRefreshing = syncing)
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }


    var searchQuery by remember { mutableStateOf("") }
    var selectedFilters by remember { mutableStateOf(setOf<NoteColorFilter>()) }
    var selectedSort by remember { mutableStateOf(NoteSort.ByDateDesc) }
    var showSortMenu by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }

    val selectedNotes by viewModel.selectedNotes.collectAsState()
    val isSelectionMode = selectedNotes.isNotEmpty()

    fun toggleFilter(filter: NoteColorFilter) {
        selectedFilters = if (selectedFilters.contains(filter)) {
            selectedFilters - filter
        } else {
            selectedFilters + filter
        }
    }

    BackHandler(enabled = isSelectionMode) {
        viewModel.clearSelection()
    }


    LaunchedEffect(shouldSync) {
        if (shouldSync) {
            viewModel.syncServer()
            profileViewModel.syncHandled()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TopAppBar(
                title = {
                    if (isSelectionMode) {
                        Text("Выбрано: ${selectedNotes.size}")
                    } else {
                        Text("Заметки")
                    }
                },
                actions = {
                    if (isSelectionMode) {
                        IconButton(onClick = { viewModel.pinSelected() }) {
                            Icon(
                                painterResource(R.drawable.ic_pin),
                                contentDescription = "Закрепить"
                            )
                        }
                        IconButton(onClick = { noteToDelete = true }) {
                            Icon(
                                painterResource(R.drawable.ic_delete),
                                contentDescription = "Удалить"
                            )
                        }
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(
                                painterResource(R.drawable.ic_close),
                                contentDescription = "Отмена"
                            )
                        }
                    } else {
                        IconButton(onClick = { onNoteClick(viewModel.createDraftNote()) }) {
                            Icon(
                                painterResource(R.drawable.ic_add_notes),
                                contentDescription = "Добавить"
                            )
                        }
                    }
                }
            )


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    placeholder = { Text("Поиск...") },
                    singleLine = true,
                    shape = RoundedCornerShape(50.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    leadingIcon = {
                        Icon(
                            painterResource(R.drawable.ic_search),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )


                Spacer(Modifier.width(8.dp))

                Box {
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(
                            painterResource(R.drawable.ic_filter),
                            contentDescription = "Фильтры",
                            tint = if (selectedFilters.isEmpty())
                                MaterialTheme.colorScheme.onSurfaceVariant
                            else
                                MaterialTheme.colorScheme.primary
                        )
                    }

                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false }
                    ) {
                        FilterCheckboxItem(
                            text = "Красные",
                            checked = selectedFilters.contains(NoteColorFilter.Red)
                        ) {
                            toggleFilter(NoteColorFilter.Red)
                        }

                        FilterCheckboxItem(
                            text = "Жёлтые",
                            checked = selectedFilters.contains(NoteColorFilter.Yellow)
                        ) {
                            toggleFilter(NoteColorFilter.Yellow)
                        }

                        FilterCheckboxItem(
                            text = "Зелёные",
                            checked = selectedFilters.contains(NoteColorFilter.Green)
                        ) {
                            toggleFilter(NoteColorFilter.Green)
                        }

                        DropdownMenuItem(
                            text = { Text("Сбросить") },
                            onClick = {
                                selectedFilters = emptySet()
                                showFilterMenu = false
                            }
                        )
                    }
                }

            }

            Box(
                modifier = Modifier.align(Alignment.End)
            ) {
                TextButton(
                    onClick = { showSortMenu = true },
                    modifier = Modifier
                        .padding(start = 16.dp)
                ) {
                    Text("Сортировка ▲▼")
                }

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Новые → старые") },
                        onClick = {
                            selectedSort = NoteSort.ByDateDesc
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Старые → новые") },
                        onClick = {
                            selectedSort = NoteSort.ByDateAsc
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Ближайшие дедлайны") },
                        onClick = {
                            selectedSort = NoteSort.ByDeadlineAsc
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("A → Z") },
                        onClick = {
                            selectedSort = NoteSort.ByTitleAsc
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Z → A") },
                        onClick = {
                            selectedSort = NoteSort.ByTitleDesc
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("По важности") },
                        onClick = {
                            selectedSort = NoteSort.ByPriority
                            showSortMenu = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            SwipeRefresh(
                state = state,
                onRefresh = {
                    if (!viewModel.isAuthorized()) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Вы не авторизованы")
                        }
                        return@SwipeRefresh
                    }
                    viewModel.syncServer()
                }
            ) {
                val filteredNotes = viewModel.filteredNotes(notes, searchQuery, selectedFilters)

                val sortedNotes = viewModel.sortNotes(filteredNotes, selectedSort)

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(sortedNotes) { note ->
                        NoteItem(
                            note = note,
                            isSelected = selectedNotes.contains(note.id),
                            selectionMode = isSelectionMode,
                            onClick = { onNoteClick(note.id) },
                            onLongPress = { viewModel.toggleSelection(note.id) },
                            onToggleDone = { viewModel.toggleDone(note) }
                        )
                    }
                }
            }
            if (noteToDelete) {
                AlertDialog(
                    onDismissRequest = { noteToDelete = false },
                    title = {
                        Text(
                            "Удалить выбранные заметки?",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    text = {
                        Text(
                            "Вы уверены? Это действие нельзя отменить.\n" +
                                    "Выбрано: ${selectedNotes.size}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    dismissButton = {
                        TextButton(onClick = { noteToDelete = false }) {
                            Text("Отмена")
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.deleteSelected()
                                noteToDelete = false
                            }
                        ) {
                            Text(
                                "Удалить",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun FilterCheckboxItem(
    text: String,
    checked: Boolean,
    onCheckedChange: () -> Unit
) {
    DropdownMenuItem(
        text = { Text(text) },
        trailingIcon = {
            androidx.compose.material3.Checkbox(
                checked = checked,
                onCheckedChange = { onCheckedChange() }
            )
        },
        onClick = { onCheckedChange() }
    )
}
