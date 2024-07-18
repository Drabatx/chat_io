package com.drabatx.chatio.data.domain.repository

import com.drabatx.chatio.data.model.ChatModel
import com.drabatx.chatio.utils.Result
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun sendMessage(message: String): Flow<Result<Unit>>
    suspend fun getAllMessages(): Flow<Result<ChatModel>>
}