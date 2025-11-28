package com.example.data.remote

import com.example.domain.model.Note
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseNoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : NoteRemoteDataSource {

    private fun userId(): String? = auth.currentUser?.uid

    override fun isAuthorized(): Boolean = auth.currentUser != null

    override suspend fun saveNoteRemote(note: Note) {
        val uid = userId() ?: return
        firestore.collection("users")
            .document(uid)
            .collection("notes")
            .document(note.id)
            .set(note.toRemote())
            .await()
    }

    override suspend fun getNotesRemote(): List<NoteRemote> {
        val uid = userId() ?: return emptyList()
        val snapshot = firestore.collection("users")
            .document(uid)
            .collection("notes")
            .get()
            .await()
        return snapshot.toObjects(NoteRemote::class.java)
    }

    override suspend fun deleteNoteRemote(noteId: String) {
        val uid = userId() ?: return
        firestore.collection("users")
            .document(uid)
            .collection("notes")
            .document(noteId)
            .delete()
            .await()
    }
}
