package com.drabatx.chatio.data.domain.usecase

import android.net.Uri
import com.drabatx.chatio.data.domain.repository.ChatRepository
import com.drabatx.chatio.utils.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendImageUseCase @Inject constructor(private val repository: ChatRepository) {
    suspend operator fun invoke(imageUri: Uri): Flow<Result<Unit>> {
        return repository.sendImage(imageUri)
    }
}