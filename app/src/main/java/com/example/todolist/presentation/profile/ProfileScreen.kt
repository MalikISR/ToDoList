package com.example.todolist.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todolist.presentation.auth.AuthScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    var showAuth by remember { mutableStateOf(false) }
    val userInfo by viewModel.userInfo.collectAsState()

    if (showAuth) {
        AuthScreen(
            onAuthSuccess = {
                showAuth = false
                viewModel.loginSuccess()
            }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Профиль") },
                actions = {
                    if (userInfo != null) {
                        TextButton(onClick = { viewModel.logout() }) {
                            Text("Выйти", color = MaterialTheme.colorScheme.error)
                        }
                    } else {
                        TextButton(onClick = { showAuth = true }) {
                            Text("Войти", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (userInfo != null) {
                ProfileCard(
                    name = userInfo!!.name ?:"_",
                    email = userInfo!!.email ?: "Не указано",
                    uid = userInfo!!.id ?: "Неизвестно"
                )
            } else {
                Text(
                    "Вы не вошли в аккаунт",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
