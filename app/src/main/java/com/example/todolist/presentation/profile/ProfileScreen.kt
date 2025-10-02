package com.example.todolist.presentation.profile


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen() {
    val user = FirebaseAuth.getInstance().currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (user != null) {
            Text(text = "UID: ${user.uid}", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(8.dp))
            Text(text = "Email: ${user.email ?: "Не указано"}")
            Spacer(Modifier.height(16.dp))
            Button(onClick = { FirebaseAuth.getInstance().signOut() }) {
                Text("Выйти")
            }
        } else {
            Text("Вы не авторизованы")
        }
    }
}