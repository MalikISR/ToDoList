package com.example.todolist.ui.navigation

sealed class Screen (val route: String) {
    object Auth : Screen("auth")
    object NoteList: Screen("note_list")
    object NoteDetail: Screen("note_detail/{noteId}"){
        fun createRoute(noteId: Int?) = "note_detail/$noteId"
    }
    object EmptyNoteDetail : Screen("note_detail_new")
}