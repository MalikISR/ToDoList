package com.example.todolist.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

object NotificationChannels {
    const val CHANNEL_DEADLINES = "channel_deadlines"
    const val CHANNEL_PRODUCTIVITY = "channel_productivity"

    fun createAll(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val deadlineChannel = NotificationChannel(
            CHANNEL_DEADLINES,
            "Дедлайны задач",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Напоминания о дедлайнах заметок"
        }

        val productivityChannel = NotificationChannel(
            CHANNEL_PRODUCTIVITY,
            "Таймер продуктивности",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Работа таймера помодоро и секундомера"
            setSound(null, null)
            enableVibration(false)
            enableLights(false)
        }

        manager.createNotificationChannel(deadlineChannel)
        manager.createNotificationChannel(productivityChannel)
    }
}
