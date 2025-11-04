package com.example.data.remote

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
            .set(note.toRemote())
            .await()
    }

    suspend fun getNotesRemote(): List<NoteRemote> {
        val uid = userId() ?: return emptyList()
        val snapshot = firestore.collection("users")
            .document(uid)
            .collection("notes")
            .get()
            .await()
        return snapshot.toObjects(NoteRemote::class.java)
    }

    suspend fun deleteNoteRemote(noteId: Int) {
        val uid = userId() ?: return
        firestore.collection("users")
            .document(uid)
            .collection("notes")
            .document(noteId.toString())
            .delete()
            .await()
    }
}
