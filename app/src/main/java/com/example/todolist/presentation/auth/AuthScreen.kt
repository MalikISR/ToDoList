package com.example.todolist.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todolist.R

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onAuthSuccess: () -> Unit
) {
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val success by viewModel.success.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var isLoginMode by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(success) {
        if (success) {
            onAuthSuccess()
            viewModel.resetStatus()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = if (isLoginMode) "Вход" else "Регистрация",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (error != null) {
            Text(
                text = error ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                if (!isLoginMode) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Имя") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Пароль") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation =
                        if (passwordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        autoCorrect = false
                    ),
                    keyboardActions = KeyboardActions(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                painterResource(
                                    if (passwordVisible) R.drawable.ic_visibility
                                    else R.drawable.ic_visibility_off
                                ),
                                contentDescription = "Показать пароль"
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    supportingText = {},
                )

                Button(
                    onClick = {
                        if (isLoginMode) {
                            viewModel.login(email, password)
                        } else {
                            viewModel.register(email, password, name)
                        }
                    },
                    enabled = !loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        if (isLoginMode) "Войти" else "Создать аккаунт",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        TextButton(
            onClick = { isLoginMode = !isLoginMode },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(
                if (isLoginMode)
                    "Нет аккаунта? Зарегистрироваться"
                else
                    "Уже есть аккаунт? Войти"
            )
        }
    }
}
