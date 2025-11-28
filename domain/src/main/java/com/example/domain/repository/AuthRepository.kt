package com.example.domain.repository

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun register(email: String, password: String, name: String): Result<Unit>
    suspend fun logout()
    fun isAuthorized(): Boolean
    fun getUserName(): String?
    fun getUserId(): String?
}

