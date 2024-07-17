package com.drabatx.chatio.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drabatx.chatio.data.domain.usecase.LoginUseCase
import com.drabatx.chatio.data.model.response.LoginResponse
import com.drabatx.chatio.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginUseCase: LoginUseCase) : ViewModel() {

    enum class LOGINSTATE {
        INITIAL, ERROR_EMAIL, ERROR_PASSWORD, SUCCESS
    }

    private val _loginMutableStateFlow = MutableStateFlow<Result<LoginResponse>>(Result.Initial)
    val loginStateFlow: StateFlow<Result<LoginResponse>> get() = _loginMutableStateFlow

    private val _isValidData = MutableStateFlow<LOGINSTATE>(LOGINSTATE.INITIAL)
    val isValidData: StateFlow<LOGINSTATE> get() = _isValidData

    private fun login(userName: String, password: String) {
        viewModelScope.launch {
            try {
                loginUseCase(userName, password).collect { result ->
                    _loginMutableStateFlow.value = result
                }
            } catch (e: Exception) {
                _loginMutableStateFlow.value = Result.Error(Throwable("Failed login: ${e.message}"))
            }
        }

    }

    fun resetForm(){
        _isValidData.value = LOGINSTATE.INITIAL
    }

    fun isValidData(emailAddress: String, password: String) {
        return if (!isEmailCorrect(emailAddress)) {
            _isValidData.value = LOGINSTATE.ERROR_EMAIL
        } else if (!isPasswordCorrect(password)) {
            _isValidData.value = LOGINSTATE.ERROR_PASSWORD
        } else {
            _isValidData.value = LOGINSTATE.SUCCESS
            login(emailAddress, password)
        }
    }

    private fun isEmailCorrect(emailAddress: String): Boolean {
        val emailRegex = Regex("""[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""")
        return emailRegex.matches(emailAddress)
    }

    private fun isPasswordCorrect(password: String): Boolean {
        val passwordRegex = Regex("^(?=.*[A-Z])(?=.*\\d).+\$")
        return passwordRegex.matches(password)
    }

}