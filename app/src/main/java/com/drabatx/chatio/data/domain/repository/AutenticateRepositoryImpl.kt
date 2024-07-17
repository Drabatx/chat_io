package com.drabatx.chatio.data.domain.repository

import android.content.SharedPreferences
import com.drabatx.chatio.data.mappers.FirebaseUserToUserModelMapper
import com.drabatx.chatio.data.model.UserModel
import com.drabatx.chatio.di.NetworkConstants
import com.drabatx.chatio.utils.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AutenticateRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val firebaseAuth: FirebaseAuth
) :
    AutenticateRepository {
    override suspend fun saveLogin(email: String, isLogged: Boolean) {
        with(sharedPreferences.edit()) {
            putString(NetworkConstants.EMAIL, email)
            putBoolean(NetworkConstants.IS_LOGGED, isLogged)
            apply()
        }
    }

    override fun isLogged(): Boolean =
        sharedPreferences.getBoolean(NetworkConstants.IS_LOGGED, false)


    override suspend fun logout() = withContext(Dispatchers.IO) {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }

    override suspend fun getCurrentUserSession(): Flow<Result<UserModel>> = flow {
        emit(Result.Loading)
        try {
            val currentUser =
                firebaseAuth.currentUser ?: throw NullPointerException("No hay una sesion activa")
            val userModel = FirebaseUserToUserModelMapper.map(currentUser)
            emit(Result.Success(userModel))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    suspend fun updateDisplayName(newName: String): Flow<Result<UserModel>> = flow {
        emit(Result.Loading)
        try {
            var currentUser =
                firebaseAuth.currentUser ?: throw NullPointerException("No hay una sesion activa")
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build()
            currentUser.updateProfile(profileUpdates).await()
            currentUser =
                firebaseAuth.currentUser ?: throw NullPointerException("No hay una sesion activa")
            Result.Success(FirebaseUserToUserModelMapper.map(currentUser))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    suspend fun updatePhotoUrl(newPhotoUrl: String): Flow<Result<UserModel>> = flow {
        emit(Result.Loading)
        try {
            var currentUser =
                firebaseAuth.currentUser ?: throw NullPointerException("No hay una sesión activa")
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setPhotoUri(android.net.Uri.parse(newPhotoUrl))
                .build()
            currentUser.updateProfile(profileUpdates).await()
            currentUser =
                firebaseAuth.currentUser ?: throw NullPointerException("No hay una sesión activa")
            emit(Result.Success(FirebaseUserToUserModelMapper.map(currentUser)))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}