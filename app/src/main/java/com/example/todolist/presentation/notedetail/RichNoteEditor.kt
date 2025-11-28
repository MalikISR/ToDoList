package com.example.todolist.presentation.notedetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolist.R
import com.example.todolist.ui.theme.richTextColorPalette
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
    val state = rememberRichTextState()
    var previewMode by remember { mutableStateOf(true) }
    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(initialHtml) {
        if (!initialized) {
            state.setHtml(initialHtml.ifBlank { "<p></p>" })
            initialized = true
        }
    }

    Scaffold(
        modifier = modifier.imePadding(),
        bottomBar = {
            RichEditorToolbar(
                state = state,
                onTogglePreview = { previewMode = !previewMode },
                isPreview = previewMode
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (previewMode) {
                RichText(
                    state = state,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            previewMode = false
                        }
                )
            } else {
                RichTextEditor(state = state, modifier = Modifier.fillMaxSize())
            }
        }
        LaunchedEffect(state.annotatedString) {
            onHtmlChange(state.toHtml())
        }
    }
}

@Composable
fun RichEditorToolbar(
    state: RichTextState,
    onTogglePreview: () -> Unit,
    isPreview: Boolean
) {
    var showFontSizeMenu by remember { mutableStateOf(false) }
    var showFontFamilyMenu by remember { mutableStateOf(false) }
    var showAlignMenu by remember { mutableStateOf(false) }

    var showTextColorDialog by remember { mutableStateOf(false) }
    var showBgColorDialog by remember { mutableStateOf(false) }

    val scroll = rememberScrollState()

    val cs = state.currentSpanStyle
    val cp = state.currentParagraphStyle

    val textColor = cs.color.takeIf { it != Color.Unspecified }
    val bgColor = cs.background.takeIf { it != Color.Unspecified }

    val isBold = cs.fontWeight == FontWeight.Bold
    val isItalic = cs.fontStyle == FontStyle.Italic
    val isUnderline = cs.textDecoration == TextDecoration.Underline
    val isStrike = cs.textDecoration == TextDecoration.LineThrough

    val isOrderedList = state.isOrderedList
    val isUnorderedList = state.isUnorderedList

    val currentAlign = cp.textAlign

    val alignIcon = when (currentAlign) {
        TextAlign.Center -> R.drawable.ic_align_center
        TextAlign.Right -> R.drawable.ic_align_right
        else -> R.drawable.ic_align_left
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        IconButton(onClick = onTogglePreview) {
            Icon(
                painter = if (isPreview)
                    painterResource(R.drawable.ic_edit)
                else painterResource(R.drawable.ic_preview),
                contentDescription = null
            )
        }

        AnimatedVisibility(visible = !isPreview) {

            Row(
                modifier = Modifier.horizontalScroll(scroll),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                ToolbarToggle(state, SpanStyle(fontWeight = FontWeight.Bold), R.drawable.ic_bold, isBold)
                ToolbarToggle(state, SpanStyle(fontStyle = FontStyle.Italic), R.drawable.ic_italic, isItalic)
                ToolbarToggle(state, SpanStyle(textDecoration = TextDecoration.Underline), R.drawable.ic_underline, isUnderline)
                ToolbarToggle(state, SpanStyle(textDecoration = TextDecoration.LineThrough), R.drawable.ic_line_through, isStrike)

                IconButton(onClick = { showTextColorDialog = true }) {
                    Icon(
                        painterResource(R.drawable.ic_text_color),
                        null,
                        tint = textColor ?: LocalContentColor.current
                    )
                }

                IconButton(onClick = { showBgColorDialog = true }) {
                    Icon(
                        painterResource(R.drawable.ic_text_background_color),
                        null,
                        tint = bgColor ?: LocalContentColor.current
                    )
                }

                if (showTextColorDialog) {
                    val defaultTextColor = MaterialTheme.colorScheme.onSurface
                    ColorPaletteDialog(
                        currentColor = textColor,
                        title = "Цвет текста",
                        onDismiss = { showTextColorDialog = false },
                        onSelect = { color ->
                            if (color == null) {
                                state.toggleSpanStyle(
                                    SpanStyle(color = defaultTextColor)
                                )
                            } else {
                                state.toggleSpanStyle(SpanStyle(color = color))
                            }
                        }
                    )
                }

                if (showBgColorDialog) {
                    ColorPaletteDialog(
                        currentColor = bgColor,
                        title = "Цвет фона",
                        onDismiss = { showBgColorDialog = false },
                        onSelect = { color ->
                            if (color == null) {
                                state.toggleSpanStyle(
                                    SpanStyle(background = Color.Transparent)
                                )
                            } else {
                                state.toggleSpanStyle(SpanStyle(background = color))
                            }
                        }
                    )
                }

                Box {
                    IconButton(onClick = { showFontSizeMenu = true }) {
                        val size = cs.fontSize.takeIf { it != TextUnit.Unspecified } ?: 16.sp
                        Text("${size.value.toInt()}pt")
                    }
                    DropdownMenu(
                        expanded = showFontSizeMenu,
                        onDismissRequest = { showFontSizeMenu = false },
                        modifier = Modifier.heightIn(max = 240.dp)
                    ) {
                        (8..72 step 1).forEach { size ->
                            DropdownMenuItem(
                                text = { Text("${size}pt", fontSize = 16.sp) },
                                onClick = {
                                    state.toggleSpanStyle(SpanStyle(fontSize = size.sp))
                                    showFontSizeMenu = false
                                }
                            )
                        }
                    }
                }

                Box {
                    IconButton(onClick = { showFontFamilyMenu = true }) {
                        Text("Aa", fontFamily = cs.fontFamily ?: FontFamily.Default, fontSize = 18.sp)
                    }
                    DropdownMenu(
                        expanded = showFontFamilyMenu,
                        onDismissRequest = { showFontFamilyMenu = false }
                    ) {
                        listOf(
                            "System Default" to FontFamily.Default,
                            "Sans" to FontFamily.SansSerif,
                            "Serif" to FontFamily.Serif,
                            "Mono" to FontFamily.Monospace,
                            "Cursive" to FontFamily.Cursive
                        ).forEach { (name, family) ->
                            DropdownMenuItem(
                                text = { Text(name, fontFamily = family) },
                                onClick = {
                                    state.toggleSpanStyle(SpanStyle(fontFamily = family))
                                    showFontFamilyMenu = false
                                }
                            )
                        }
                    }
                }

                Box {
                    IconButton(onClick = { showAlignMenu = true }) {
                        Icon(painterResource(alignIcon), null)
                    }
                    DropdownMenu(
                        expanded = showAlignMenu,
                        onDismissRequest = { showAlignMenu = false }
                    ) {

                        @Composable
                        fun alignOption(text: String, icon: Int, align: TextAlign) {
                            DropdownMenuItem(
                                text = { Text(text) },
                                leadingIcon = { Icon(painterResource(icon), null) },
                                onClick = {
                                    applyWithSelection(state) {
                                        state.toggleParagraphStyle(
                                            ParagraphStyle(textAlign = align)
                                        )
                                    }
                                    showAlignMenu = false
                                }
                            )
                        }

                        alignOption("Left", R.drawable.ic_align_left, TextAlign.Left)
                        alignOption("Center", R.drawable.ic_align_center, TextAlign.Center)
                        alignOption("Right", R.drawable.ic_align_right, TextAlign.Right)
                    }
                }

                IconButton(
                    onClick = { applyWithSelection(state) { state.toggleUnorderedList() } },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = if (isUnorderedList) MaterialTheme.colorScheme.primary else LocalContentColor.current
                    )
                ) { Icon(painterResource(R.drawable.ic_bulleted_list), null) }

                IconButton(
                    onClick = { applyWithSelection(state) { state.toggleOrderedList() } },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = if (isOrderedList) MaterialTheme.colorScheme.primary else LocalContentColor.current
                    )
                ) { Icon(painterResource(R.drawable.ic_numbered_list), null) }
            }
        }
    }
}

fun applyWithSelection(state: RichTextState, block: () -> Unit) {
    val sel = state.selection
    block()
    state.selection = sel
}

@Composable
fun ToolbarToggle(
    state: RichTextState,
    span: SpanStyle,
    icon: Int,
    isActive: Boolean
) {
    IconButton(
        onClick = {
            state.toggleSpanStyle(span)
        },
        colors = IconButtonDefaults.iconButtonColors(
            contentColor =
                if (isActive) MaterialTheme.colorScheme.primary
                else LocalContentColor.current
        )
    ) {
        Icon(painterResource(icon), null)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorPaletteDialog(
    currentColor: Color?,
    onDismiss: () -> Unit,
    onSelect: (Color?) -> Unit,
    title: String
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )

                val colors = richTextColorPalette()

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    colors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(color, CircleShape)
                                .border(
                                    width = if (color == currentColor) 3.dp else 1.dp,
                                    color = if (color == currentColor)
                                        MaterialTheme.colorScheme.primary
                                    else Color.DarkGray,
                                    shape = CircleShape
                                )
                                .clickable {
                                    onSelect(color)
                                    onDismiss()
                                }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    androidx.compose.material3.Button(
                        onClick = {
                            onSelect(null)
                            onDismiss()
                        }
                    ) {
                        Text("Сбросить")
                    }
                }
            }
        }
    )
}
