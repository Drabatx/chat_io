package com.drabatx.chatio.data.domain.usecase

import com.drabatx.chatio.data.domain.repository.ChatRepository
import com.drabatx.chatio.utils.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(private val repository: ChatRepository) {
    suspend operator fun invoke(message: String): Flow<Result<Unit>> {
        return repository.sendMessage(message)
    }
}