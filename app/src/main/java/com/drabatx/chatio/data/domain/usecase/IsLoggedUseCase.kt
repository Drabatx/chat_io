package com.drabatx.chatio.data.domain.usecase

import com.drabatx.chatio.data.domain.repository.LoginRepository
import com.drabatx.chatio.utils.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsLoggedUseCase @Inject constructor(private val repository: LoginRepository) {
    operator fun invoke(): Boolean {
        return repository.isLogged()
    }
}