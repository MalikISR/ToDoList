package com.example.todolist.presentation.productivity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PomodoroTimerScreen(
    state: PomodoroState,
    onToggle: () -> Unit,
    onReset: () -> Unit,
    onClassicPreset: () -> Unit,
    onLongPreset: () -> Unit
) {
    val minutes = state.remainingSeconds / 60
    val seconds = state.remainingSeconds % 60

    val phaseTitle = when (state.phase) {
        PomodoroPhase.FOCUS -> "Фокус"
        PomodoroPhase.SHORT_BREAK -> "Короткий перерыв"
        PomodoroPhase.LONG_BREAK -> "Длинный перерыв"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = phaseTitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Сессий фокуса: ${state.completedFocusSessions}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "%02d:%02d".format(minutes, seconds),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = onClassicPreset,
                    label = { Text("25 / 5 / 15") }
                )
                AssistChip(
                    onClick = onLongPreset,
                    label = { Text("50 / 10 / 30") }
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Фокус: ${state.config.focusMinutes} мин · Перерыв: ${state.config.shortBreakMinutes}/${state.config.longBreakMinutes} мин",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onToggle,
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.large
            ) {
                Text(if (state.isRunning) "Пауза" else "Старт")
            }

            OutlinedButton(
                onClick = onReset,
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.large
            ) {
                Text("Сброс")
            }
        }
    }
}
