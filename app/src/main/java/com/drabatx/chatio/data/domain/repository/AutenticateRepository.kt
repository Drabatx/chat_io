package com.drabatx.chatio.data.domain.repository

interface AutenticateRepository {
    suspend fun saveLogin(email: String, isLogged: Boolean)
    fun isLogged(): Boolean
    suspend fun logout()
}