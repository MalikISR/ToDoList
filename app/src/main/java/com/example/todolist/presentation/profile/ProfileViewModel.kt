package com.example.todolist.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.UserInfo
import com.example.domain.usecase.auth.GetUserInfoUseCase
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
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val syncNotesUseCase: SyncNotesUseCase,
    private val isAuthorizedUseCase: IsAuthorizedUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo: StateFlow<UserInfo?> = _userInfo

    private val _shouldSync = MutableStateFlow(false)
    val shouldSync: StateFlow<Boolean> = _shouldSync

    init {
        refreshUser()
    }

    private fun refreshUser() {
        _userInfo.value = if (isAuthorizedUseCase()) {
            getUserInfoUseCase()
        } else {
            null
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

    private fun syncNotes() {
        viewModelScope.launch {
            syncNotesUseCase()
            _shouldSync.value = true
        }
    }

    fun syncHandled() {
        _shouldSync.value = false
    }
}