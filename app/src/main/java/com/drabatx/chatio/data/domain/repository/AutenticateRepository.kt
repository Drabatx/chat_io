package com.drabatx.chatio.data.domain.repository

import com.drabatx.chatio.data.model.UserModel
import com.drabatx.chatio.utils.Result
import kotlinx.coroutines.flow.Flow

interface AutenticateRepository {
    suspend fun saveLogin(email: String, isLogged: Boolean)
    fun isLogged(): Boolean
    suspend fun logout()
    suspend fun getCurrentUserSession(): Flow<Result<UserModel>>

}