package com.drabatx.chatio.data.domain.repository

import com.drabatx.chatio.data.model.UserModel
import com.drabatx.chatio.utils.Result
import kotlinx.coroutines.flow.Flow

interface AutenticateRepository {
    fun startSession(userModel: UserModel, isLogged: Boolean)
    fun isLogged(): Boolean
    fun getCurrentUser(): UserModel
    fun logout()
    suspend fun getCurrentUserSession(): Flow<Result<UserModel>>

}