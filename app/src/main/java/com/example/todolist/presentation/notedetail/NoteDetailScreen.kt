package com.example.todolist.presentation.notedetail

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todolist.R
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
        var deadline by remember(note) { mutableStateOf(note.deadline) }
        var pinned by remember(note) { mutableStateOf(note.isPinned) }
        var showDateTimePicker by remember { mutableStateOf(false) }
        val savedEvent by viewModel.savedEvent.collectAsState()
        val iconColor by animateColorAsState(
            targetValue = if (savedEvent) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface,
            animationSpec = tween(durationMillis = 300),
            label = "SaveIconColor"
        )

        LaunchedEffect(title, description, selectedColor, deadline, pinned) {
            val updated = note.copy(
                title = title,
                description = description,
                color = selectedColor,
                deadline = deadline,
                isPinned = pinned
            )
            viewModel.onNoteChanged(updated)
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Заметка") },
                    navigationIcon = {
                        IconButton(onClick = {
                            viewModel.saveBeforeLeave()
                            onBack()
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            viewModel.saveBeforeLeave()
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_save),
                                contentDescription = "Сохранить",
                                tint = iconColor
                            )
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
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(10.dp)
                        .shadow(0.dp, RoundedCornerShape(12.dp))
                ) {

                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("Введите заголовок...") },
                        textStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(4.dp),
                        singleLine = true,
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(selectedColor))
                    )

                    Spacer(Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(6.dp)
                    ) {
                        RichNoteEditor(
                            initialHtml = description,
                            onHtmlChange = { html -> description = html },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 200.dp)
                        )
                    }
                }
            }

            if (showSettings) {
                ModalBottomSheet(
                    onDismissRequest = { showSettings = false }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            "Настройки заметки",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        Text(
                            "Приоритет заметки",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            PriorityColorOption(
                                color = Color.Red,
                                name = "Срочная",
                                selected = selectedColor == Color.Red.toArgb(),
                                onClick = { selectedColor = Color.Red.toArgb() }
                            )

                            PriorityColorOption(
                                color = Color.Yellow,
                                name = "Важная",
                                selected = selectedColor == Color.Yellow.toArgb(),
                                onClick = { selectedColor = Color.Yellow.toArgb() }
                            )

                            PriorityColorOption(
                                color = Color.Green,
                                name = "Обычная",
                                selected = selectedColor == Color.Green.toArgb(),
                                onClick = { selectedColor = Color.Green.toArgb() }
                            )
                        }

                        Spacer(Modifier.height(30.dp))

                        Text(
                            "Дедлайн",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { showDateTimePicker = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = formatFullDateRu(deadline),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(Modifier.height(20.dp))

                        if (showDateTimePicker) {
                            val context = LocalContext.current
                            val cal = Calendar.getInstance()

                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    TimePickerDialog(
                                        context,
                                        { _, hour, minute ->
                                            val c = Calendar.getInstance()
                                            c.set(year, month, day, hour, minute)
                                            deadline = c.timeInMillis
                                            showDateTimePicker = false
                                        },
                                        cal.get(Calendar.HOUR_OF_DAY),
                                        cal.get(Calendar.MINUTE),
                                        true
                                    ).show()
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }

                        Spacer(Modifier.height(32.dp))
                    }
                }
            }

        }
    }
}

@Composable
fun PriorityColorOption(
    color: Color,
    name: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {

        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(color)
                .border(
                    width = if (selected) 4.dp else 2.dp,
                    color = if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    shape = CircleShape
                )
                .clickable { onClick() }
        )

        Spacer(Modifier.height(8.dp))

        Text(
            name,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

fun formatFullDateRu(time: Long): String {
    val formatter = SimpleDateFormat("d MMMM yyyy, HH:mm", Locale("ru"))
    return formatter.format(Date(time))
}
