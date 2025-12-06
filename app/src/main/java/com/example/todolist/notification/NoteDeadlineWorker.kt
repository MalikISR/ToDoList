package com.example.todolist.notification

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.todolist.MainActivity
import com.example.todolist.R

class NoteDeadlineWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val noteId = inputData.getString(KEY_NOTE_ID) ?: return Result.failure()
        val noteTitle = inputData.getString(KEY_NOTE_TITLE) ?: "Задача"

        showNotification(noteId, noteTitle)
        return Result.success()
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(noteId: String, noteTitle: String) {
        val context = applicationContext

        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra(EXTRA_NOTE_ID, noteId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            noteId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NotificationChannels.CHANNEL_DEADLINES)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle("Скоро дедлайн")
            .setContentText("Для заметки \"$noteTitle\" скоро наступит дедлайн")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context)
            .notify(noteId.hashCode(), notification)
    }

    companion object {
        const val KEY_NOTE_ID = "note_id"
        const val KEY_NOTE_TITLE = "note_title"
        const val EXTRA_NOTE_ID = "extra_note_id"
    }
}
