package com.example.domain.usecase.auth

import com.example.domain.model.UserInfo
import com.example.domain.repository.AuthRepository
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): UserInfo {
        return UserInfo(
            name = authRepository.getUserName(),
            email = authRepository.getUserEmail(),
            id = authRepository.getUserId()
        )
    }
}
