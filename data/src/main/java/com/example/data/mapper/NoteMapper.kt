package com.example.data.mapper

import com.example.domain.model.Note
import com.example.data.model.NoteEntity

fun NoteEntity.toDomain(): Note = Note(
    id = id,
    title = title,
    description = description,
    deadline = deadline,
    color = color,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isDeleted = isDeleted,
    isPinned = isPinned,
)

fun Note.toEntity(): NoteEntity = NoteEntity(
    id = id,
    title = title,
    description = description,
    deadline = deadline,
    color = color,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isDeleted = isDeleted,
    isPinned = isPinned,
)