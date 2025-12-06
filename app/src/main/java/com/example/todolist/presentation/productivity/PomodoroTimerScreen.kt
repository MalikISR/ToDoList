package com.example.todolist.presentation.productivity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.todolist.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroTimerScreen(
    state: PomodoroState,
    onToggle: () -> Unit,
    onReset: () -> Unit,
    onStartCustom: (Int, Int) -> Unit
) {
    val minutes = state.remainingSeconds / 60
    val seconds = state.remainingSeconds % 60

    var showSheet by remember { mutableStateOf(false) }

    // При открытии модалки подставляем текущие значения
    var workInput by remember { mutableStateOf(state.config.focusMinutes.toString()) }
    var restInput by remember { mutableStateOf(state.config.breakMinutes.toString()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ▼ Центральный блок теперь центрируется вертикально
        Column(
            modifier = Modifier
                .weight(1f)   // ← занимает всё доступное пространство
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "%02d:%02d".format(minutes, seconds),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = {
                        workInput = "25"; restInput = "5"
                        onStartCustom(25, 5)
                    },
                    label = { Text("25 / 5") }
                )

                AssistChip(
                    onClick = {
                        workInput = "50"; restInput = "10"
                        onStartCustom(50, 10)
                    },
                    label = { Text("50 / 10") }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ▼ Нижний блок кнопок остаётся снизу
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = {
                    workInput = state.config.focusMinutes.toString()
                    restInput = state.config.breakMinutes.toString()
                    showSheet = true
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
            ) {
                Icon(
                    painterResource(R.drawable.ic_settings),
                    contentDescription = "Настройки",
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Button(
                onClick = onToggle,
                modifier = Modifier
                    .weight(4f)
                    .height(50.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text(if (state.isRunning) "Пауза" else "Старт")
            }

            OutlinedButton(
                onClick = onReset,
                modifier = Modifier
                    .weight(4f)
                    .height(50.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text("Сброс")
            }
        }
    }


    // ---------- BOTTOM SHEET ----------
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    "Кастомный таймер",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                // Красивые поля — мягкий фон, округление
                CustomInputField(
                    value = workInput,
                    label = "Время работы (мин)",
                    onChange = { workInput = it.filter(Char::isDigit) }
                )

                CustomInputField(
                    value = restInput,
                    label = "Время отдыха (мин)",
                    onChange = { restInput = it.filter(Char::isDigit) }
                )

                Button(
                    onClick = {
                        val focus = workInput.toIntOrNull() ?: 25
                        val rest = restInput.toIntOrNull() ?: 5
                        onStartCustom(focus, rest)
                        showSheet = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    enabled = workInput.isNotBlank() && restInput.isNotBlank()
                ) {
                    Text("Запустить")
                }

                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun CustomInputField(
    value: String,
    label: String,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}

