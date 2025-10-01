package com.example.todolist.repository

import com.example.domain.model.Note
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseNoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private fun userId(): String? = auth.currentUser?.uid

    suspend fun saveNoteRemote(note: Note) {
        val uid = userId() ?: return
        firestore.collection("users")
            .document(uid)
            .collection("notes")
            .document(note.id.toString())
            .set(note)
            .await()
    }

    suspend fun getNotesRemote(): List<Note> {
        val uid = userId() ?: return emptyList()
        val snapshot = firestore.collection("users")
            .document(uid)
            .collection("notes")
            .get()
            .await()
        return snapshot.toObjects(Note::class.java)
    }
}