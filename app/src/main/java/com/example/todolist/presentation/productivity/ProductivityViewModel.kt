package com.example.todolist.presentation.productivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class PomodoroPhase {
    FOCUS,
    SHORT_BREAK,
    LONG_BREAK
}

data class PomodoroConfig(
    val focusMinutes: Int,
    val shortBreakMinutes: Int,
    val longBreakMinutes: Int,
    val sessionsBeforeLongBreak: Int
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

class ProductivityViewModel : ViewModel() {

    private val defaultConfig = PomodoroConfig(
        focusMinutes = 25,
        shortBreakMinutes = 5,
        longBreakMinutes = 15,
        sessionsBeforeLongBreak = 4
    )

    private val _pomodoroState = MutableStateFlow(
        PomodoroState(
            remainingSeconds = defaultConfig.focusMinutes * 60,
            isRunning = false,
            phase = PomodoroPhase.FOCUS,
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

    private var pomodoroJob: Job? = null
    private var stopwatchJob: Job? = null

    fun togglePomodoro() {
        val state = _pomodoroState.value
        if (state.isRunning) {
            pausePomodoro()
        } else {
            startPomodoro()
        }
    }

    private fun startPomodoro() {
        if (pomodoroJob?.isActive == true) return

        _pomodoroState.update { it.copy(isRunning = true) }

        pomodoroJob = viewModelScope.launch {
            while (isActive && _pomodoroState.value.isRunning) {
                delay(1000)
                tickPomodoro()
            }
        }
    }

    private fun pausePomodoro() {
        _pomodoroState.update { it.copy(isRunning = false) }
        pomodoroJob?.cancel()
        pomodoroJob = null
    }

    fun resetPomodoro() {
        pausePomodoro()
        _pomodoroState.update { state ->
            state.copy(
                remainingSeconds = state.config.focusMinutes * 60,
                phase = PomodoroPhase.FOCUS,
                completedFocusSessions = 0
            )
        }
    }

    private fun tickPomodoro() {
        val state = _pomodoroState.value
        if (state.remainingSeconds > 0) {
            _pomodoroState.update { it.copy(remainingSeconds = it.remainingSeconds - 1) }
        } else {
            onPhaseFinished()
        }
    }

    private fun onPhaseFinished() {
        val state = _pomodoroState.value
        when (state.phase) {
            PomodoroPhase.FOCUS -> {
                val newCompleted = state.completedFocusSessions + 1
                val nextPhase =
                    if (newCompleted % state.config.sessionsBeforeLongBreak == 0)
                        PomodoroPhase.LONG_BREAK
                    else
                        PomodoroPhase.SHORT_BREAK

                val nextSeconds = when (nextPhase) {
                    PomodoroPhase.SHORT_BREAK -> state.config.shortBreakMinutes * 60
                    PomodoroPhase.LONG_BREAK -> state.config.longBreakMinutes * 60
                    else -> state.config.focusMinutes * 60
                }

                _pomodoroState.update {
                    it.copy(
                        phase = nextPhase,
                        remainingSeconds = nextSeconds,
                        completedFocusSessions = newCompleted
                    )
                }
            }

            PomodoroPhase.SHORT_BREAK, PomodoroPhase.LONG_BREAK -> {
                _pomodoroState.update {
                    it.copy(
                        phase = PomodoroPhase.FOCUS,
                        remainingSeconds = it.config.focusMinutes * 60
                    )
                }
            }
        }
    }

    private fun setPomodoroPreset(preset: PomodoroConfig) {
        pausePomodoro()
        _pomodoroState.update {
            PomodoroState(
                remainingSeconds = preset.focusMinutes * 60,
                isRunning = false,
                phase = PomodoroPhase.FOCUS,
                completedFocusSessions = 0,
                config = preset
            )
        }
    }

    fun classicPreset() {
        setPomodoroPreset(
            PomodoroConfig(
                focusMinutes = 25,
                shortBreakMinutes = 5,
                longBreakMinutes = 15,
                sessionsBeforeLongBreak = 4
            )
        )
    }

    fun longPreset() {
        setPomodoroPreset(
            PomodoroConfig(
                focusMinutes = 50,
                shortBreakMinutes = 10,
                longBreakMinutes = 30,
                sessionsBeforeLongBreak = 2
            )
        )
    }

    fun toggleStopwatch() {
        val state = _stopwatchState.value
        if (state.isRunning) {
            pauseStopwatch()
        } else {
            startStopwatch()
        }
    }

    private fun startStopwatch() {
        if (stopwatchJob?.isActive == true) return

        _stopwatchState.update { it.copy(isRunning = true) }

        stopwatchJob = viewModelScope.launch {
            while (isActive && _stopwatchState.value.isRunning) {
                delay(1000)
                _stopwatchState.update { s -> s.copy(elapsedSeconds = s.elapsedSeconds + 1) }
            }
        }
    }

    private fun pauseStopwatch() {
        _stopwatchState.update { it.copy(isRunning = false) }
        stopwatchJob?.cancel()
        stopwatchJob = null
    }

    fun resetStopwatch() {
        pauseStopwatch()
        _stopwatchState.update { StopwatchState(elapsedSeconds = 0, isRunning = false) }
    }

    // endregion
}
