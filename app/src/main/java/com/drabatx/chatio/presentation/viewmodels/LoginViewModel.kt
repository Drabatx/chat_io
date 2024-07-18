package com.drabatx.chatio.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drabatx.chatio.data.domain.usecase.IsLoggedUseCase
import com.drabatx.chatio.data.domain.usecase.LoginUseCase
import com.drabatx.chatio.data.domain.usecase.RegisterUseCase
import com.drabatx.chatio.data.model.response.LoginResponse
import com.drabatx.chatio.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val isLoggedUseCase: IsLoggedUseCase
) : ViewModel() {

    enum class LOGIN_STATE {
        INITIAL, ERROR_EMAIL, ERROR_PASSWORD, SUCCESS
    }

    enum class LOGIN_OPERATION {
        LOGIN, REGISTER
    }

    private val _loginMutableStateFlow = MutableStateFlow<Result<LoginResponse>>(Result.Initial)
    val loginStateFlow: StateFlow<Result<LoginResponse>> get() = _loginMutableStateFlow

    private val _isValidData = MutableStateFlow(LOGIN_STATE.INITIAL)
    val isValidData: StateFlow<LOGIN_STATE> get() = _isValidData

    private val _isLoggedStateFlow = MutableStateFlow(false)
    val isLoggedStateFlow: StateFlow<Boolean> get() = _isLoggedStateFlow

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

    fun register(userName: String, password: String) {
        viewModelScope.launch {
            try {
                registerUseCase(userName, password).collect { result ->
                    _loginMutableStateFlow.value = result
                }
            } catch (e: Exception) {
                _loginMutableStateFlow.value =
                    Result.Error(Throwable("Failed register: ${e.message}"))
            }
        }
    }

    fun resetForm() {
        _isValidData.value = LOGIN_STATE.INITIAL
    }

    fun isValidData(
        emailAddress: String,
        password: String,
        operation: LOGIN_OPERATION? = LOGIN_OPERATION.LOGIN
    ) {
        return if (!isEmailCorrect(emailAddress)) {
            _isValidData.value = LOGIN_STATE.ERROR_EMAIL
        } else if (!isPasswordCorrect(password)) {
            _isValidData.value = LOGIN_STATE.ERROR_PASSWORD
        } else {
            _isValidData.value = LOGIN_STATE.SUCCESS
            when (operation) {
                LOGIN_OPERATION.LOGIN -> {
                    login(emailAddress, password)
                }

                LOGIN_OPERATION.REGISTER -> {
                    register(emailAddress, password)
                }

                null -> {}
            }
        }
    }

    fun isLogged(){
        viewModelScope.launch {
            _isLoggedStateFlow.value = isLoggedUseCase()
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

    fun resetData() {
        _loginMutableStateFlow.value = Result.Initial
        _isValidData.value = LOGIN_STATE.INITIAL
        _isLoggedStateFlow.value = false
    }

}


