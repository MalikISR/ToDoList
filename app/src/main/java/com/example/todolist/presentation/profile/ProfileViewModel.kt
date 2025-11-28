package com.example.todolist.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.repository.AuthRepository
import com.example.domain.usecase.auth.IsAuthorizedUseCase
import com.example.domain.usecase.auth.LogoutUseCase
import com.example.domain.usecase.note.SyncNotesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val syncNotesUseCase: SyncNotesUseCase,
    private val isAuthorizedUseCase: IsAuthorizedUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow(authRepository.getUserName())
    val currentUserName: StateFlow<String?> = _currentUser

    private val _userEmail = MutableStateFlow(authRepository.getUserId())
    val userId: StateFlow<String?> = _userEmail

    private val _shouldSync = MutableStateFlow(false)
    val shouldSync: StateFlow<Boolean> = _shouldSync


    fun refreshUser() {
        if (isAuthorizedUseCase()) {
            _currentUser.value = authRepository.getUserName()
            _userEmail.value = authRepository.getUserId()
        } else {
            _currentUser.value = null
            _userEmail.value = null
        }
    }

    fun loginSuccess() {
        refreshUser()
        syncNotes()
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            refreshUser()
            _shouldSync.value = true
        }
    }

    fun syncNotes() {
        viewModelScope.launch {
            syncNotesUseCase()
            _shouldSync.value = true
        }
    }

    fun syncHandled() {
        _shouldSync.value = false
    }
}