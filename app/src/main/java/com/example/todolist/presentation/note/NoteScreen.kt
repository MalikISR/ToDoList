package com.example.todolist.presentation.note

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todolist.R


@Composable
fun NoteScreen(
    viewModel: NoteViewModel = hiltViewModel(),
    onNoteClick: (Int?) -> Unit,
    onAddNoteClick: () -> Unit
) {
    val notes by viewModel.notes.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Заметки",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f).width(150.dp)
            )

            IconButton( onClick = { onAddNoteClick() },
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add_notes),
                    contentDescription = "Добавить"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(notes) { note ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .clickable { onNoteClick(note.id) },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = note.title.ifBlank { "Без заголовка" },
                                style = MaterialTheme.typography.bodyLarge
                            )
                            if (note.description.isNotBlank()) {
                                Text(
                                    text = note.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    maxLines = 2
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.deleteNote(note) }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_delete),
                                contentDescription = "Удалить"
                            )
                        }
                    }
                }
            }
        }
    }
}
