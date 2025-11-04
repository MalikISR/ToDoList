package com.example.data.remote

import com.example.domain.model.Note

data class NoteRemote(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val deadline: Long = 0,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val color: Int = 0,
    val isPinned: Boolean = false,
    val isDeleted: Boolean = false
)

fun Note.toRemote(): NoteRemote = NoteRemote(
    id = id,
    title = title,
    description = description,
    deadline = deadline,
    createdAt = createdAt,
    updatedAt = updatedAt,
    color = color,
    isPinned = isPinned,
    isDeleted = isDeleted
)

fun NoteRemote.toDomain(): Note = Note(
    id = id,
    title = title,
    description = description,
    deadline = deadline,
    createdAt = createdAt,
    updatedAt = updatedAt,
    color = color,
    isPinned = isPinned,
    isDeleted = isDeleted
)
