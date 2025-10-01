package com.example.data.mapper

import com.example.domain.model.Note
import com.example.data.model.NoteEntity

fun NoteEntity.toDomain(): Note = Note(
    id = id,
    title = title,
    description = description,
    timestamp = timestamp,
    color = color,
    isPinned = isPinned,
)

fun Note.toEntity(): NoteEntity = NoteEntity(
    id = id,
    title = title,
    description = description,
    timestamp = timestamp,
    color = color,
    isPinned = isPinned,
)