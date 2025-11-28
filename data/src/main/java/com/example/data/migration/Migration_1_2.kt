package com.example.data.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {

        database.execSQL("""
            CREATE TABLE notes_new (
                id TEXT NOT NULL,
                title TEXT NOT NULL,
                description TEXT NOT NULL,
                deadline INTEGER NOT NULL,
                createdAt INTEGER NOT NULL,
                updatedAt INTEGER NOT NULL,
                color INTEGER NOT NULL,
                isPinned INTEGER NOT NULL,
                isDeleted INTEGER NOT NULL,
                isSynced INTEGER NOT NULL,
                isDone INTEGER NOT NULL,
                PRIMARY KEY(id)
            )
        """)

        database.execSQL("""
            INSERT INTO notes_new (
                id, title, description, deadline,
                createdAt, updatedAt, color,
                isPinned, isDeleted, isSynced, isDone
            )
            SELECT
                CAST(id AS TEXT),    -- ⚠ тут Room не поможет, делаем вручную
                title,
                description,
                deadline,
                createdAt,
                updatedAt,
                color,
                isPinned,
                isDeleted,
                isSynced,
                isDone
            FROM notes
        """)

        database.execSQL("DROP TABLE notes")
        database.execSQL("ALTER TABLE notes_new RENAME TO notes")
    }
}
