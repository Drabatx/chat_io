package com.drabatx.chatio.data.domain.usecase

import com.drabatx.chatio.data.domain.repository.AutenticateRepository
import javax.inject.Inject

class LogOutUseCase @Inject constructor(private val repository: AutenticateRepository) {
    operator fun invoke() {
        repository.logout()
    }
}

