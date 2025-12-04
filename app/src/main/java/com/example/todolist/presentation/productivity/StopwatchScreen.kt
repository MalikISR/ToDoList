package com.example.todolist.presentation.productivity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
fun StopwatchScreen(
    state: StopwatchState,
    onToggle: () -> Unit,
    onReset: () -> Unit
) {
    val minutes = state.elapsedSeconds / 60
    val seconds = state.elapsedSeconds % 60

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(1.dp))

        Text(
            text = "%02d:%02d".format(minutes, seconds),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.SemiBold
        )

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
