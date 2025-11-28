package com.example.todolist.utils

import android.text.Html

fun htmlToPlainText(html: String): String {
    return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        .toString()
        .trim()
}