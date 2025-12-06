package com.example.todolist.presentation.productivity

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.todolist.MainActivity
import com.example.todolist.R
import com.example.todolist.notification.NotificationChannels
import kotlinx.coroutines.*

class PomodoroService : Service() {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null

    private lateinit var currentConfig: PomodoroConfig
    private var currentPhase: PomodoroPhase = PomodoroPhase.WORK
    private var currentCompleted: Int = 0
    private var currentRemaining: Int = 0
    private val currentTime = System.currentTimeMillis()
    private var isPaused = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {

            ACTION_START -> {
                val cfg = PomodoroConfig(
                    focusMinutes = intent.getIntExtra("focus", 25),
                    breakMinutes = intent.getIntExtra("break", 5)
                )

                val phase = PomodoroPhase.valueOf(
                    intent.getStringExtra("phase") ?: PomodoroPhase.WORK.name
                )

                val completed = intent.getIntExtra("completed", 0)
                val remaining = intent.getIntExtra("remaining", cfg.focusMinutes * 60)

                startPomodoro(cfg, phase, completed, remaining)
            }

            ACTION_RESUME -> {
                if (::currentConfig.isInitialized) {
                    startPomodoro(
                        config = currentConfig,
                        startPhase = currentPhase,
                        completedSessions = currentCompleted,
                        remainingSeconds = currentRemaining
                    )
                }
            }

            ACTION_PAUSE -> pausePomodoro()

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

    private fun startPomodoro(
        config: PomodoroConfig,
        startPhase: PomodoroPhase,
        completedSessions: Int,
        remainingSeconds: Int
    ) {
        job?.cancel()

        currentConfig = config
        currentPhase = startPhase
        currentCompleted = completedSessions
        currentRemaining = remainingSeconds

        startForeground(
            NOTIF_ID,
            buildNotification("Помодоро", formatTime(currentRemaining), isRunning = true)
        )

        job = scope.launch {
            while (isActive) {

                broadcastState(true)
                updateNotification("Помодоро", formatTime(currentRemaining), true)

                delay(1000)
                currentRemaining--

                if (currentRemaining <= 0) {
                    playFinishSound()

                    currentPhase = if (currentPhase == PomodoroPhase.WORK)
                        PomodoroPhase.BREAK
                    else
                        PomodoroPhase.WORK

                    currentRemaining = if (currentPhase == PomodoroPhase.WORK)
                        currentConfig.focusMinutes * 60
                    else
                        currentConfig.breakMinutes * 60
                }
            }
        }
    }

    private fun pausePomodoro() {
        job?.cancel()
        job = null
        isPaused = true

        broadcastState(false)
        updateNotification("Помодоро", formatTime(currentRemaining), false)
        stopForeground(STOP_FOREGROUND_DETACH)
    }

    private fun broadcastState(isRunning: Boolean) {
        if (!::currentConfig.isInitialized) return

        val intent = Intent(ACTION_STATE).apply {
            putExtra("running", isRunning)
            putExtra("remaining", currentRemaining)
            putExtra("phase", currentPhase.name)
            putExtra("completed", currentCompleted)

            putExtra("focus", currentConfig.focusMinutes)
            putExtra("break", currentConfig.breakMinutes)
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
        val i = Intent(this, PomodoroService::class.java).apply {
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
        job?.cancel()
        scope.cancel()
        super.onDestroy()
    }

    private fun playFinishSound() {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val ringtone = RingtoneManager.getRingtone(applicationContext, uri)
        ringtone.play()

        // остановим через 3 секунды
        scope.launch {
            delay(3000)
            ringtone.stop()
        }
    }

    companion object {
        const val ACTION_START = "pomodoro_start"
        const val ACTION_RESUME = "pomodoro_resume"
        const val ACTION_PAUSE = "pomodoro_pause"
        const val ACTION_STOP = "pomodoro_stop"
        const val ACTION_STATE = "pomodoro_state"

        const val NOTIF_ID = 2201

        private fun formatTime(seconds: Int): String {
            val m = seconds / 60
            val s = seconds % 60
            return "%02d:%02d".format(m, s)
        }

        fun start(
            context: Context,
            config: PomodoroConfig,
            phase: PomodoroPhase,
            completed: Int,
            remainingSeconds: Int
        ) {
            val i = Intent(context, PomodoroService::class.java).apply {
                action = ACTION_START

                putExtra("focus", config.focusMinutes)
                putExtra("break", config.breakMinutes)

                putExtra("phase", phase.name)
                putExtra("completed", completed)
                putExtra("remaining", remainingSeconds)
            }
            context.startForegroundService(i)
        }

        fun pause(context: Context) {
            val i = Intent(context, PomodoroService::class.java).apply {
                action = ACTION_PAUSE
            }
            context.startService(i)
        }

        fun stop(context: Context) {
            val i = Intent(context, PomodoroService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(i)
        }
    }
}
