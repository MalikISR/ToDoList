package com.example.domain.model

data class Note (
    val id:Int = 0,
    val title: String,
    val description: String,
    val timestamp: Long,
    val color: Int,
    val isPinned: Boolean
)