package com.example.todolist.presentation.notedetail

import android.net.Uri
import com.example.todolist.R
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor


@Composable
fun RichNoteEditor(
    initialHtml: String,
    modifier: Modifier = Modifier,
    onHtmlChange: (String) -> Unit
) {
    val richState = rememberRichTextState()
    var previewMode by remember { mutableStateOf(true) }

    // Загружаем HTML при открытии
    LaunchedEffect(initialHtml) {
        richState.setHtml(initialHtml.ifBlank { "<p></p>" })
    }

    // Лаунчер для выбора изображения
    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val imgHtml = """<img src="$it"/>"""
            val oldHtml = richState.toHtml()

            val newHtml =
                if (oldHtml.contains("</p>"))
                    oldHtml.replace("</p>", "$imgHtml</p>")
                else
                    oldHtml + imgHtml

            richState.setHtml(newHtml)
        }
    }

    Scaffold(
        modifier = modifier.imePadding(),     // Поднимает контент над клавиатурой
        bottomBar = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()            // ← Держит панель над клавиатурой
            ) {
                RichEditorToolbar(
                    state = richState,
                    onPickImage = { imagePicker.launch("image/*") },
                    onTogglePreview = { previewMode = !previewMode },
                    isPreview = previewMode
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (previewMode) {
                RichText(
                    state = richState,
                    modifier = Modifier
                        .fillMaxSize()
                )
            } else {
                RichTextEditor(
                    state = richState,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            // Обновляем HTML при изменении содержимого
            LaunchedEffect(richState.annotatedString) {
                onHtmlChange(richState.toHtml())
            }
        }
    }
}

@Composable
fun RichEditorToolbar(
    state: RichTextState,
    onPickImage: () -> Unit,
    onTogglePreview: () -> Unit,
    isPreview: Boolean
) {
    val currentSpanStyle = state.currentSpanStyle
    val currentParagraphStyle = state.currentParagraphStyle

    val isBold = currentSpanStyle.fontWeight == FontWeight.Bold
    val isItalic = currentSpanStyle.fontStyle == FontStyle.Italic
    val isUnderlined = currentSpanStyle.textDecoration == TextDecoration.Underline
    val isStrikethrough = currentSpanStyle.textDecoration == TextDecoration.LineThrough

    val isOrderedList = state.isOrderedList
    val isUnorderedList = state.isUnorderedList

    val currentAlign = currentParagraphStyle.textAlign ?: TextAlign.Left

    val scrollState = rememberScrollState()

    var showTextColorPicker by remember { mutableStateOf(false) }
    var showBackgroundColorPicker by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .background(Color(0xFFEFEFEF), RoundedCornerShape(6.dp))
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Переключатель "редактирование / предпросмотр"
        IconButton(onClick = onTogglePreview) {
            Icon(
                painter =
                    if (isPreview) painterResource(R.drawable.ic_edit)
                    else painterResource(R.drawable.ic_preview),
                contentDescription = "Предпросмотр"
            )
        }

        // Жирный
        IconButton(
            onClick = {
                state.toggleSpanStyle(
                    SpanStyle(fontWeight = FontWeight.Bold)
                )
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_bold),
                contentDescription = "Жирный",
                tint = if (isBold) MaterialTheme.colorScheme.primary else Color.Unspecified
            )
        }

        // Курсив
        IconButton(
            onClick = {
                state.toggleSpanStyle(
                    SpanStyle(fontStyle = FontStyle.Italic)
                )
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_italic),
                contentDescription = "Курсив",
                tint = if (isItalic) MaterialTheme.colorScheme.primary else Color.Unspecified
            )
        }

        // Подчеркнутый
        IconButton(
            onClick = {
                state.toggleSpanStyle(
                    SpanStyle(textDecoration = TextDecoration.Underline)
                )
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_underline),
                contentDescription = "Подчеркнутый",
                tint = if (isUnderlined) MaterialTheme.colorScheme.primary else Color.Unspecified
            )
        }

        // Зачеркнутый
        IconButton(
            onClick = {
                state.toggleSpanStyle(
                    SpanStyle(textDecoration = TextDecoration.LineThrough)
                )
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_line_through),
                contentDescription = "Зачеркнутый",
                tint = if (isStrikethrough) MaterialTheme.colorScheme.primary else Color.Unspecified
            )
        }

        // Маркированный список
        IconButton(
            onClick = { state.toggleUnorderedList() }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_bulleted_list),
                contentDescription = "Маркированный список",
                tint = if (isUnorderedList) MaterialTheme.colorScheme.primary else Color.Unspecified
            )
        }

        // Нумерованный список
        IconButton(
            onClick = { state.toggleOrderedList() }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_numbered_list),
                contentDescription = "Нумерованный список",
                tint = if (isOrderedList) MaterialTheme.colorScheme.primary else Color.Unspecified
            )
        }

        // Выравнивание слева
        IconButton(
            onClick = {
                state.toggleParagraphStyle(
                    ParagraphStyle(textAlign = TextAlign.Left)
                )
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_align_left),
                contentDescription = "По левому краю",
                tint =
                    if (currentAlign == TextAlign.Left) MaterialTheme.colorScheme.primary
                    else Color.Unspecified
            )
        }

        // По центру
        IconButton(
            onClick = {
                state.toggleParagraphStyle(
                    ParagraphStyle(textAlign = TextAlign.Center)
                )
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_align_center),
                contentDescription = "По центру",
                tint = if (currentAlign == TextAlign.Center) MaterialTheme.colorScheme.primary else Color.Unspecified
            )
        }

        // По правому краю
        IconButton(
            onClick = {
                state.toggleParagraphStyle(
                    ParagraphStyle(textAlign = TextAlign.Right)
                )
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_align_right),
                contentDescription = "По правому краю",
                tint = if (currentAlign == TextAlign.Right) MaterialTheme.colorScheme.primary else Color.Unspecified
            )
        }

        // Цвет текста
        IconButton(onClick = { showTextColorPicker = true }) {
            Icon(
                painter = painterResource(R.drawable.ic_color_text),
                contentDescription = "Цвет текста"
            )
        }

        // Цвет фона текста
        IconButton(onClick = { showBackgroundColorPicker = true }) {
            Icon(
                painter = painterResource(R.drawable.ic_text_background_color),
                contentDescription = "Цвет подложки"
            )
        }

        // Картинка
        IconButton(onClick = onPickImage) {
            Icon(
                painter = painterResource(R.drawable.ic_image),
                contentDescription = "Добавить изображение"
            )
        }

    }

    // Диалог выбора цвета текста
    if (showTextColorPicker) {
        ColorPickerDialog(
            initialColor = Color.Black,
            onDismiss = { showTextColorPicker = false },
            onColorSelected = {
                state.addSpanStyle(SpanStyle(color = it))
                showTextColorPicker = false
            }
        )
    }

    // Диалог выбора цвета фона
    if (showBackgroundColorPicker) {
        ColorPickerDialog(
            initialColor = Color.Yellow,
            onDismiss = { showBackgroundColorPicker = false },
            onColorSelected = {
                state.addSpanStyle(SpanStyle(background = it))
                showBackgroundColorPicker = false
            }
        )
    }
}
