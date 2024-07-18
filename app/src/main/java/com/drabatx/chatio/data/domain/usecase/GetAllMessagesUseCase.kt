package com.drabatx.chatio.data.domain.usecase

import com.drabatx.chatio.data.domain.repository.ChatRepository
import com.drabatx.chatio.data.model.ChatModel
import com.drabatx.chatio.utils.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllMessagesUseCase @Inject constructor(private val repository: ChatRepository) {
    suspend operator fun invoke(): Flow<Result<ChatModel>> {
        return repository.getAllMessages()
    }
}