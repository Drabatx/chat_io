package com.drabatx.chatio.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drabatx.chatio.data.domain.usecase.GetAllMessagesUseCase
import com.drabatx.chatio.data.domain.usecase.LogOutUseCase
import com.drabatx.chatio.data.domain.usecase.SendMessageUseCase
import com.drabatx.chatio.data.model.ChatModel
import com.drabatx.chatio.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val logOutUseCase: LogOutUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getAllMessagesUseCase: GetAllMessagesUseCase
) : ViewModel() {
    private val _sendMessageState = MutableStateFlow<Result<Unit>>(Result.Initial)
    val sendMessageState: StateFlow<Result<Unit>> get() = _sendMessageState
    private val _getAllMesagesState = MutableStateFlow<Result<ChatModel>>(Result.Loading)
    val getAllMesagesState: StateFlow<Result<ChatModel>> get() = _getAllMesagesState
    fun logOut() {
        viewModelScope.launch {
            logOutUseCase()
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            sendMessageUseCase(message).collect { result ->
                _sendMessageState.value = result
            }
        }
    }

    fun getAllMessages() {
        viewModelScope.launch {
            getAllMessagesUseCase().collect { result ->
                _getAllMesagesState.value = result
            }
        }
    }
}