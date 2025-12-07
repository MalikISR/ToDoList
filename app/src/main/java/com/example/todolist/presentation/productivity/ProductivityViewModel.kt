package com.example.todolist.presentation.productivity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

enum class PomodoroPhase { WORK, BREAK }

data class PomodoroConfig(
    val focusMinutes: Int,
    val breakMinutes: Int
)

data class PomodoroState(
    val remainingSeconds: Int,
    val isRunning: Boolean,
    val phase: PomodoroPhase,
    val completedFocusSessions: Int,
    val config: PomodoroConfig
)

data class StopwatchState(
    val elapsedSeconds: Int,
    val isRunning: Boolean
)

@HiltViewModel
class ProductivityViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val defaultConfig = PomodoroConfig(
        focusMinutes = 25,
        breakMinutes = 5
    )

    private val _pomodoroState = MutableStateFlow(
        PomodoroState(
            remainingSeconds = defaultConfig.focusMinutes * 60,
            isRunning = false,
            phase = PomodoroPhase.WORK,
            completedFocusSessions = 0,
            config = defaultConfig
        )
    )
    val pomodoroState: StateFlow<PomodoroState> = _pomodoroState.asStateFlow()

    private val _stopwatchState = MutableStateFlow(
        StopwatchState(
            elapsedSeconds = 0,
            isRunning = false
        )
    )
    val stopwatchState: StateFlow<StopwatchState> = _stopwatchState.asStateFlow()

    private val timerReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context, intent: Intent) {
            when (intent.action) {

                PomodoroService.ACTION_STATE -> {
                    val cfg = PomodoroConfig(
                        focusMinutes = intent.getIntExtra("focus", defaultConfig.focusMinutes),
                        breakMinutes = intent.getIntExtra("break", defaultConfig.breakMinutes)
                    )

                    _pomodoroState.update {
                        it.copy(
                            remainingSeconds = intent.getIntExtra("remaining", it.remainingSeconds),
                            isRunning = intent.getBooleanExtra("running", false),
                            phase = PomodoroPhase.valueOf(intent.getStringExtra("phase") ?: it.phase.name),
                            completedFocusSessions = intent.getIntExtra("completed", it.completedFocusSessions),
                            config = cfg
                        )
                    }
                }

                StopwatchService.ACTION_STATE -> {
                    _stopwatchState.update {
                        it.copy(
                            elapsedSeconds = intent.getIntExtra("elapsed", it.elapsedSeconds),
                            isRunning = intent.getBooleanExtra("running", false)
                        )
                    }
                }
            }
        }
    }

    init {
        val filter = IntentFilter().apply {
            addAction(PomodoroService.ACTION_STATE)
            addAction(StopwatchService.ACTION_STATE)
        }

        ContextCompat.registerReceiver(
            context,
            timerReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onCleared() {
        context.unregisterReceiver(timerReceiver)
        super.onCleared()
    }

    fun startCustomPomodoro(work: Int, rest: Int) {
        val cfg = PomodoroConfig(work, rest)

        _pomodoroState.update {
            it.copy(
                config = cfg,
                remainingSeconds = work * 60,
                phase = PomodoroPhase.WORK,
                completedFocusSessions = 0,
                isRunning = false
            )
        }

        PomodoroService.start(
            context = context,
            config = cfg,
            phase = PomodoroPhase.WORK,
            completed = 0,
            remainingSeconds = work * 60
        )
    }

    fun togglePomodoro() {
        val state = _pomodoroState.value
        if (state.isRunning) {
            PomodoroService.pause(context)
        } else {
            PomodoroService.start(
                context = context,
                config = state.config,
                phase = state.phase,
                completed = state.completedFocusSessions,
                remainingSeconds = state.remainingSeconds
            )
        }
    }

    fun resetPomodoro() {
        PomodoroService.stop(context)
        _pomodoroState.update {
            it.copy(
                remainingSeconds = it.config.focusMinutes * 60,
                isRunning = false,
                phase = PomodoroPhase.WORK,
                completedFocusSessions = 0
            )
        }
    }

    fun toggleStopwatch() {
        val state = _stopwatchState.value
        if (state.isRunning) {
            StopwatchService.pause(context)
        } else {
            StopwatchService.start(context, state.elapsedSeconds)
        }
    }

    fun resetStopwatch() {
        StopwatchService.stop(context)
        _stopwatchState.update {
            StopwatchState(0, false)
        }
    }
}