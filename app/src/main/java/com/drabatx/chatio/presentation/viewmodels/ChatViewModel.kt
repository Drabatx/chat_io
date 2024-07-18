package com.drabatx.chatio.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drabatx.chatio.data.domain.usecase.GetAllMessagesUseCase
import com.drabatx.chatio.data.domain.usecase.LogOutUseCase
import com.drabatx.chatio.data.domain.usecase.SendImageUseCase
import com.drabatx.chatio.data.domain.usecase.SendMessageUseCase
import com.drabatx.chatio.data.model.ChatModel
import com.drabatx.chatio.data.model.MessageModel
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
    private val getAllMessagesUseCase: GetAllMessagesUseCase,
    private val sendImageUseCase: SendImageUseCase
) : ViewModel() {
    private val _sendMessageState = MutableStateFlow<Result<Unit>>(Result.Initial)
    val sendMessageState: StateFlow<Result<Unit>> get() = _sendMessageState

    private val _getAllMesagesState = MutableStateFlow<Result<ChatModel>>(Result.Loading)
    val getAllMesagesState: StateFlow<Result<ChatModel>> get() = _getAllMesagesState

    private val _messages = mutableListOf<MessageModel>()
    val messages: List<MessageModel> get() = _messages.toList()

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
            try {
                _getAllMesagesState.value = Result.Loading
                getAllMessagesUseCase().collect { result ->
                    when (result) {
                        is Result.Success -> {
                            val newMessages = result.data.messages
                            // Agregar solo mensajes que no están en _messages
                            newMessages.forEach { message ->
                                if (!_messages.contains(message)) {
                                    _messages.add(0, message)
                                }
                            }
                            _getAllMesagesState.value = Result.Success(result.data)
                        }

                        is Result.Error -> {
                            _getAllMesagesState.value = Result.Error(result.exception)
                        }

                        else -> {}
                        // Puedes manejar Result.Initial y Result.Loading si es necesario
                    }
                }
            } catch (e: Exception) {
                _getAllMesagesState.value = Result.Error(e)
            }
        }
    }

    fun sendImageMessage(imageUri: Uri) {
        viewModelScope.launch {
            sendImageUseCase(imageUri).collect{
                _sendMessageState.value = it
            }
        }
    }
}