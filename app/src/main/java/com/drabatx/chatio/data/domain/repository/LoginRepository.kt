package com.drabatx.chatio.data.domain.repository

import com.drabatx.chatio.data.model.response.LoginResponse
import com.drabatx.chatio.utils.Result
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    suspend fun login(username: String, password: String): Flow<Result<LoginResponse>>
    suspend fun register(userName: String, password: String): Flow<Result<LoginResponse>>
    fun isLogged(): Boolean
}
