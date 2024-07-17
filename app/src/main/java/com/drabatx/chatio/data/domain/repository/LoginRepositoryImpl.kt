package com.drabatx.chatio.data.domain.repository

import com.drabatx.chatio.data.model.response.LoginResponse
import com.drabatx.chatio.data.model.response.RegisterResponse
import com.drabatx.chatio.utils.Result
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(private val firebaseAuth: FirebaseAuth) :
    LoginRepository {
    override suspend fun login(userName: String, password: String): Flow<Result<LoginResponse>> =
        flow {
            emit(Result.Loading)
            try {
                val authResult =
                    firebaseAuth.signInWithEmailAndPassword(userName, password)
                        .await()
                if (authResult.user != null) {
                    val email = authResult.user!!.email ?: ""
                    emit(Result.Success(LoginResponse("Login Success", email)))
                } else {
                    emit(Result.Error(Throwable("Failed to authenticate user")))
                }
            } catch (e: Exception) {
                emit(Result.Error(e))
            }
        }

    override suspend fun register(
        userName: String,
        password: String
    ): Flow<Result<RegisterResponse>> = flow {
        emit(Result.Loading)
        try {
            val authResult =
                firebaseAuth.createUserWithEmailAndPassword(userName, password)
                    .await()
            if (authResult.user != null) {
                val email = authResult.user!!.email ?: ""
                emit(Result.Success(RegisterResponse("Login Success", email)))
            } else {
                emit(Result.Error(Throwable("Failed to authenticate user")))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

}