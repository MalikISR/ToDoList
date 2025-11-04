package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity (
    @PrimaryKey(autoGenerate = true)
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