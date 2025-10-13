package com.example.todolist.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.example.todolist.presentation.auth.AuthScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    val user = FirebaseAuth.getInstance().currentUser
    var currentUser by remember { mutableStateOf(user) }
    var showAuthScreen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Профиль") },
                actions = {
                    if (currentUser != null) {
                        TextButton(onClick = {
                            FirebaseAuth.getInstance().signOut()
                            currentUser = null
                        }) {
                            Text("Выйти", color = MaterialTheme.colorScheme.error)
                        }
                    } else {
                        TextButton(onClick = { showAuthScreen = true }) {
                            Text("Войти", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (currentUser != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Имя: ${currentUser?.displayName ?: "Не указано"}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text("Email: ${currentUser?.email ?: "Не указано"}")
                        Text("UID: ${currentUser?.uid}")
                    }
                }
            } else if (showAuthScreen) {
                AuthScreen(
                    onAuthSuccess = { uid ->
                        currentUser = FirebaseAuth.getInstance().currentUser
                        showAuthScreen = false
                    }
                )
            } else {
                Text(
                    text = "Вы не вошли в аккаунт",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
