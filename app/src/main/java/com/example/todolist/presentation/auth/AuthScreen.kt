package com.example.todolist.presentation.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

@Composable
fun AuthScreen(
    onAuthSuccess: (userId: String) -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var isLoginMode by remember { mutableStateOf(true) }

    Column(modifier = Modifier.padding(16.dp)) {
        // Ввод email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        // Ввод имени только при регистрации
        if (!isLoginMode) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Имя") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Ввод пароля
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            modifier = Modifier.fillMaxWidth()
        )

        // Кнопка входа/регистрации
        Button(
            onClick = {
                if (isLoginMode) {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                onAuthSuccess(auth.currentUser?.uid ?: "")
                            } else {
                                Toast.makeText(
                                    context,
                                    "Ошибка входа: ${task.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build()
                                auth.currentUser?.updateProfile(profileUpdates)
                                    ?.addOnCompleteListener {
                                        onAuthSuccess(auth.currentUser?.uid ?: "")
                                    }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Ошибка регистрации: ${task.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(if (isLoginMode) "Войти" else "Зарегистрироваться")
        }

        Button(
            onClick = { isLoginMode = !isLoginMode },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text(if (isLoginMode) "Нет аккаунта? Зарегистрироваться" else "Уже есть аккаунт? Войти")
        }
    }
}
