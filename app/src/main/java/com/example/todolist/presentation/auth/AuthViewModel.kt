package com.example.todolist.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.auth.LoginUseCase
import com.example.domain.usecase.auth.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loading.value = true

            val result = loginUseCase(email, password)

            _loading.value = false

            result.fold(
                onSuccess = { _success.value = true },
                onFailure = { _error.value = it.message }
            )
        }
    }

    fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            _loading.value = true

            val result = registerUseCase(email, password, name)

            _loading.value = false

            result.fold(
                onSuccess = { _success.value = true },
                onFailure = { _error.value = it.message }
            )
        }
    }

    fun resetStatus() {
        _success.value = false
        _error.value = null
    }
}

