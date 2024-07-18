package com.drabatx.chatio.data.domain.usecase

import com.drabatx.chatio.data.domain.repository.LoginRepository
import com.drabatx.chatio.data.model.response.LoginResponse
import com.drabatx.chatio.utils.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterUseCase @Inject constructor(private val repository: LoginRepository) {
    suspend operator fun invoke(
        userName: String,
        password: String
    ): Flow<Result<LoginResponse>> {
        return repository.register(userName, password)
    }
}