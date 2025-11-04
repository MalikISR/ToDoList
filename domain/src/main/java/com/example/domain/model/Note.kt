package com.example.domain.model

data class Note (
    val id:Int = 0,
    val title: String,
    val description: String,
    val deadline: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val color: Int,
    val isPinned: Boolean,
    val isDeleted: Boolean,
    val isSynced: Boolean = false
    )