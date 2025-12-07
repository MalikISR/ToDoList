package com.example.todolist.presentation.productivity

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.todolist.MainActivity
import com.example.todolist.R
import com.example.todolist.notification.NotificationChannels
import kotlinx.coroutines.*

class StopwatchService : Service() {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null
    private var elapsedSeconds: Int = 0
    private lateinit var wakeLock: WakeLock
    private val currentTime = System.currentTimeMillis()
    private var isPaused = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "todolist:timerLock")
        wakeLock.acquire()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                elapsedSeconds = intent.getIntExtra("elapsed", 0)
                startStopwatch()
            }

            ACTION_RESUME -> { startStopwatch() }

            ACTION_PAUSE -> pauseStopwatch()

            ACTION_STOP -> {
                job?.cancel()
                job = null

                if (isPaused) {
                    NotificationManagerCompat.from(this).cancel(NOTIF_ID)
                } else {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                }
                isPaused = false
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun startStopwatch() {
        job?.cancel()

        startForeground(
            NOTIF_ID,
            buildNotification("Секундомер", formatTime(elapsedSeconds), isRunning = true)
        )

        job = scope.launch {
            while (isActive) {
                broadcastState(isRunning = true)
                updateNotification("Секундомер", formatTime(elapsedSeconds), isRunning = true)
                delay(1000)
                elapsedSeconds++
            }
        }
    }

    private fun pauseStopwatch() {
        job?.cancel()
        job = null
        isPaused = true

        broadcastState(isRunning = false)
        updateNotification("Секундомер", formatTime(elapsedSeconds), isRunning = false)
        stopForeground(STOP_FOREGROUND_DETACH)
    }

    private fun broadcastState(isRunning: Boolean) {
        val intent = Intent(ACTION_STATE).apply {
            putExtra("running", isRunning)
            putExtra("elapsed", elapsedSeconds)
        }
        intent.setPackage(packageName)
        sendBroadcast(intent)
    }

    @Suppress("MissingPermission")
    private fun updateNotification(title: String, text: String, isRunning: Boolean) {
        NotificationManagerCompat.from(this)
            .notify(NOTIF_ID, buildNotification(title, text, isRunning))
    }

    private fun buildNotification(title: String, text: String, isRunning: Boolean): Notification {
        val mainIntent = Intent(this, MainActivity::class.java)
        val pending = PendingIntent.getActivity(
            this,
            0,
            mainIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val actionText = if (isRunning) "Пауза" else "Старт"
        val actionIntent = if (isRunning) ACTION_PAUSE else ACTION_RESUME

        return NotificationCompat.Builder(this, NotificationChannels.CHANNEL_PRODUCTIVITY)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_timer)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setContentIntent(pending)
            .addAction(
                0,
                actionText,
                actionPendingIntent(actionIntent)
            )
            .addAction(
                0,
                "Стоп",
                actionPendingIntent(ACTION_STOP)
            )
            .setWhen(currentTime)
            .build()
    }

    private fun actionPendingIntent(action: String): PendingIntent {
        val i = Intent(this, StopwatchService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            this,
            action.hashCode(),
            i,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun onDestroy() {
        if (wakeLock.isHeld) wakeLock.release()
        job?.cancel()
        scope.cancel()
        super.onDestroy()
    }

    companion object {
        const val ACTION_START = "stopwatch_start"
        const val ACTION_RESUME = "stopwatch_resume"
        const val ACTION_PAUSE = "stopwatch_pause"
        const val ACTION_STOP = "stopwatch_stop"
        const val ACTION_STATE = "stopwatch_state"

        const val NOTIF_ID = 2202

        private fun formatTime(seconds: Int): String {
            val m = seconds / 60
            val s = seconds % 60
            return "%02d:%02d".format(m, s)
        }

        fun start(context: Context, elapsed: Int) {
            val i = Intent(context, StopwatchService::class.java).apply {
                action = ACTION_START
                putExtra("elapsed", elapsed)
            }
            context.startForegroundService(i)
        }

        fun pause(context: Context) {
            val i = Intent(context, StopwatchService::class.java).apply {
                action = ACTION_PAUSE
            }
            context.startService(i)
        }

        fun stop(context: Context) {
            val i = Intent(context, StopwatchService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(i)
        }
    }
}
