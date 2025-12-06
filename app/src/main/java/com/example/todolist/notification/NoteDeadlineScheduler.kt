package com.example.todolist.notification

import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class NoteDeadlineScheduler(
    private val workManager: WorkManager
) {

    fun schedule(noteId: String, noteTitle: String, deadlineMillis: Long) {
        val triggerTimeMillis = deadlineMillis - ONE_HOUR_MILLIS
        val delayMillis = triggerTimeMillis - System.currentTimeMillis()

        if (delayMillis <= 0) {
            return
        }

        val data = Data.Builder()
            .putString(NoteDeadlineWorker.KEY_NOTE_ID, noteId)
            .putString(NoteDeadlineWorker.KEY_NOTE_TITLE, noteTitle)
            .build()

        val request = OneTimeWorkRequestBuilder<NoteDeadlineWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(tagForNote(noteId))
            .build()

        workManager.cancelAllWorkByTag(tagForNote(noteId))
        workManager.enqueue(request)
    }

    fun cancel(noteId: String) {
        workManager.cancelAllWorkByTag(tagForNote(noteId))
    }

    private fun tagForNote(noteId: String) = "note_deadline_$noteId"

    companion object {
        private const val ONE_HOUR_MILLIS = 60 * 60 * 1000L
    }
}
