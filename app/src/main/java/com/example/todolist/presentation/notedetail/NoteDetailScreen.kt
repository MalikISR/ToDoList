package com.example.todolist.presentation.notedetail

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    viewModel: NoteDetailViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val note = viewModel.note.collectAsState().value
    var showSettings by remember { mutableStateOf(false) }

    if (note != null) {
        var title by remember(note) { mutableStateOf(note.title) }
        var description by remember(note) { mutableStateOf(note.description) }
        var selectedColor by rememberSaveable { mutableStateOf(note.color) }
        var selectedTime by remember(note) { mutableStateOf(note.timestamp) }
        var pinned by remember(note) { mutableStateOf(note.isPinned) }

        val context = LocalContext.current

        val colors = listOf(Color.Green, Color.Yellow, Color.Red)

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Детали заметки") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            viewModel.saveNote(
                                note.copy(
                                    title = title,
                                    description = description,
                                    color = selectedColor,
                                    timestamp = selectedTime,
                                    isPinned = pinned
                                )
                            )
                        }) {
                            Icon(Icons.Default.Done, contentDescription = "Сохранить")
                        }
                        IconButton(onClick = { showSettings = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Настройки")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(Color.White) // фон страницы
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEDEDED)) // сероватый фон "границ" заметки
                        .padding(10.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    // Заголовок
                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("Введите заголовок...") },
                        textStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color.White,
                                shape = RoundedCornerShape(4.dp)
                            ), // поле текста белое
                        singleLine = true,
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        )
                    )

                    // Разделитель
                    Divider(
                        color = Color(0xFFCCCCCC),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    // Описание
                    TextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("Введите описание...") },
                        textStyle = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(
                                Color.White,
                                shape = RoundedCornerShape(4.dp)
                            ), // поле текста белое
                        singleLine = false,
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        )
                    )
                }
            }

            // Настройки (шторка)
            if (showSettings) {
                ModalBottomSheet(
                    onDismissRequest = { showSettings = false }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            "Настройки",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Цвет заметки
                        Text("Цвет заметки")
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            ColorOption(
                                color = Color.Green,
                                label = "Зелёный",
                                selected = selectedColor == Color.Green.toArgb(),
                                onClick = { selectedColor = Color.Green.toArgb() }
                            )
                            ColorOption(
                                color = Color.Yellow,
                                label = "Жёлтый",
                                selected = selectedColor == Color.Yellow.toArgb(),
                                onClick = { selectedColor = Color.Yellow.toArgb() }
                            )
                            ColorOption(
                                color = Color.Red,
                                label = "Красный",
                                selected = selectedColor == Color.Red.toArgb(),
                                onClick = { selectedColor = Color.Red.toArgb() }
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        // Выбор времени
                        Button(
                            onClick = {
                                val calendar = Calendar.getInstance()
                                val timePicker = TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        val cal = Calendar.getInstance()
                                        cal.set(Calendar.HOUR_OF_DAY, hour)
                                        cal.set(Calendar.MINUTE, minute)
                                        selectedTime = cal.timeInMillis
                                    },
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE),
                                    true
                                )
                                timePicker.show()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Выбрать время")
                        }

                        if (selectedTime != null) {
                            val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault())
                                .format(Date(selectedTime))
                            Text(
                                "Выбранное время: $formattedTime",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        // 🔹 Новый чекбокс "Закрепить заметку"
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = pinned,
                                onCheckedChange = { isChecked ->
                                    pinned = isChecked
                                }
                            )
                            Text("Закрепить заметку", style = MaterialTheme.typography.bodyLarge)
                        }

                        Spacer(Modifier.height(24.dp))

                        // Сохранение
                        Button(
                            onClick = {
                                viewModel.saveNote(
                                    note.copy(
                                        title = title,
                                        description = description,
                                        color = selectedColor,
                                        timestamp = selectedTime,
                                        isPinned = pinned
                                    )
                                )
                                showSettings = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Сохранить")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColorOption(color: Color, label: String, selected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color)
                .border(
                    width = if (selected) 4.dp else 0.dp, // обводка только у выбранного
                    color = if (selected) Color.Black else Color.Transparent,
                    shape = CircleShape
                )
                .clickable { onClick() }
        )
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}