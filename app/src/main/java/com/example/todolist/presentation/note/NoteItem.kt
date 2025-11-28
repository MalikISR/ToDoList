package com.example.todolist.presentation.note

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.domain.model.Note
import com.example.todolist.R
import com.example.todolist.utils.htmlToPlainText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NoteItem(
    note: Note,
    onClick: () -> Unit,
    onToggleDone: () -> Unit,
    onTogglePin: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier
            .graphicsLayer {
                alpha = if (note.isDone) 0.6f else 1f
            }
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {

            Box(
                modifier = Modifier
                    .width(8.dp)
                    .fillMaxHeight()
                    .background(Color(note.color))
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = note.title.ifBlank { "Без заголовка" },
                        style = MaterialTheme.typography.titleMedium.copy(
                            textDecoration =
                                if (note.isDone) TextDecoration.LineThrough
                                else TextDecoration.None,
                            color = if (note.isDone)
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                    alpha = 0.5f
                                )
                            else
                                MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.weight(1f)
                    )


                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = formatCreatedAt(note.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.width(8.dp))

                    IconButton(
                        onClick = { onToggleDone() },
                        modifier = Modifier.size(26.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = if (note.isDone)
                                colorResource(R.color.done_green)
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_done),
                            contentDescription = "Выполнено",
                            modifier = Modifier.size(22.dp),
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    IconButton(
                        onClick = { onTogglePin() },
                        modifier = Modifier.size(22.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor =
                                if (note.isPinned) colorResource(R.color.pin_active)
                                else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_pin),
                            contentDescription = "Закрепить",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }


                Spacer(Modifier.height(6.dp))

                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                        )
                )

                Spacer(Modifier.height(6.dp))

                if (note.description.isNotBlank()) {
                    Text(
                        text = htmlToPlainText(note.description),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            textDecoration =
                                if (note.isDone) TextDecoration.LineThrough
                                else TextDecoration.None,
                            color = if (note.isDone)
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                    alpha = 0.4f
                                )
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 2
                    )
                }

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val color = deadlineColor(note.deadline)
                    val timeString = formatDeadline(note.deadline)

                    Text(
                        text = timeString,
                        style = MaterialTheme.typography.bodySmall,
                        color = color
                    )


                    IconButton(
                        onClick = { onDelete() },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
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
@Composable
fun deadlineColor(deadline: Long): Color {
    val now = System.currentTimeMillis()
    val diff = deadline - now

    return when {
        diff < 0 -> MaterialTheme.colorScheme.error
        diff < 24 * 60 * 60 * 1000 -> colorResource(R.color.pin_active)
        else -> MaterialTheme.colorScheme.primary
    }
}

fun formatCreatedAt(createdAt: Long?): String {
    if (createdAt == null) return ""
    val formatter = SimpleDateFormat("dd.MM HH:mm", Locale.getDefault())
    return formatter.format(Date(createdAt))
}

fun formatDeadline(deadline: Long): String {
    val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return formatter.format(Date(deadline))
}