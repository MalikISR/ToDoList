package com.example.todolist.presentation.productivity

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun ProductivityScreen(
    viewModel: ProductivityViewModel = hiltViewModel()
) {
    val pomodoroState by viewModel.pomodoroState.collectAsState()
    val stopwatchState by viewModel.stopwatchState.collectAsState()

    val tabs = listOf("Помодоро", "Секундомер")
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize()) {

        TabRow(selectedTabIndex = pagerState.currentPage) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> PomodoroTimerScreen(
                    state = pomodoroState,
                    onToggle = { viewModel.togglePomodoro() },
                    onReset = { viewModel.resetPomodoro() },
                    onClassicPreset = { viewModel.classicPreset() },
                    onLongPreset = { viewModel.longPreset() }
                )

                1 -> StopwatchScreen(
                    state = stopwatchState,
                    onToggle = { viewModel.toggleStopwatch() },
                    onReset = { viewModel.resetStopwatch() }
                )
            }
        }
    }
}
