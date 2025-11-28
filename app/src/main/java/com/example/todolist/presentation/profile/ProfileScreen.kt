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
    val name by viewModel.currentUserName.collectAsState()
    val uid by viewModel.userId.collectAsState()


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
                    if (name != null) {
                        TextButton(onClick = { viewModel.logout() }) {
                            Text("Выйти", color = MaterialTheme.colorScheme.error)
                        }
                    } else {
                        TextButton(onClick = {
                            showAuth = true
                        }) {
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (name != null) {
                UserCard(name!!, uid!!)
            } else {
                Text("Вы не вошли в аккаунт")
            }
        }
    }
}

@Composable
fun UserCard(name: String, uid: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Имя: $name")
            Text("UID: $uid")
        }
    }
}


