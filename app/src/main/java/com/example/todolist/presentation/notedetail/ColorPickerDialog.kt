package com.example.todolist.presentation.notedetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ColorPickerDialog(
    initialColor: Color,
    onDismiss: () -> Unit,
    onColorSelected: (Color) -> Unit
) {
    var red by remember { mutableStateOf(initialColor.red) }
    var green by remember { mutableStateOf(initialColor.green) }
    var blue by remember { mutableStateOf(initialColor.blue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { androidx.compose.material3.Text("Выбор цвета") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                androidx.compose.material3.Text("Предпросмотр")

                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(Color(red, green, blue))
                )

                androidx.compose.material3.Slider(
                    value = red,
                    onValueChange = { red = it },
                    valueRange = 0f..1f
                )
                androidx.compose.material3.Slider(
                    value = green,
                    onValueChange = { green = it },
                    valueRange = 0f..1f
                )
                androidx.compose.material3.Slider(
                    value = blue,
                    onValueChange = { blue = it },
                    valueRange = 0f..1f
                )
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = {
                onColorSelected(Color(red, green, blue))
            }) {
                androidx.compose.material3.Text("OK")
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                androidx.compose.material3.Text("Отмена")
            }
        }
    )
}
