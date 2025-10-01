package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity (
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val title: String,
    val description: String,
    val timestamp: Long,
    val color: Int,
    val isPinned: Boolean
)