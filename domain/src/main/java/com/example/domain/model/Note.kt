package com.example.domain.model

import java.util.UUID

data class Note (
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val deadline: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val color: Int,
    val isPinned: Boolean,
    val isDeleted: Boolean,
    val isSynced: Boolean = false,
    val isDone: Boolean
)