package com.example.todolist.presentation.productivity

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun ProductivityScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Помодоро", "Секундомер")

    Column(Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTab == index,
                    onClick = { selectedTab = index }
                )
            }
        }

        when (selectedTab) {
            0 -> PomodoroTimer()
            1 -> Stopwatch()
        }
    }
}

@Composable
fun PomodoroTimer() {
    var timeLeft by remember { mutableStateOf(25 * 60) } // 25 мин
    var isRunning by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            isRunning = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "%02d:%02d".format(timeLeft / 60, timeLeft % 60),
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(Modifier.height(16.dp))
        Row {
            Button(onClick = { isRunning = !isRunning }) {
                Text(if (isRunning) "Стоп" else "Старт")
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { timeLeft = 25 * 60; isRunning = false }) {
                Text("Сброс")
            }
        }
    }
}

@Composable
fun Stopwatch() {
    var time by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (true) {
                delay(1000)
                time++
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "%02d:%02d".format(time / 60, time % 60),
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(Modifier.height(16.dp))
        Row {
            Button(onClick = { isRunning = !isRunning }) {
                Text(if (isRunning) "Стоп" else "Старт")
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { time = 0; isRunning = false }) {
                Text("Сброс")
            }
        }
    }
}
