package com.drabatx.chatio.data.domain.repository

import android.content.SharedPreferences
import com.drabatx.chatio.data.mappers.FirebaseUserToUserModelMapper
import com.drabatx.chatio.data.model.UserModel
import com.drabatx.chatio.di.AppConstants
import com.drabatx.chatio.utils.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class AutenticateRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val firebaseAuth: FirebaseAuth
) :
    AutenticateRepository {
    override fun startSession(userModel: UserModel, isLogged: Boolean) {
        with(sharedPreferences.edit()) {
            putString(AppConstants.NAME, userModel.name)
            putString(AppConstants.AVATAR, userModel.avatar)
            putString(AppConstants.ID, userModel.id)
            putString(AppConstants.EMAIL, userModel.email)
            putBoolean(AppConstants.IS_LOGGED, isLogged)
            apply()
        }
    }

    override fun isLogged(): Boolean =
        sharedPreferences.getBoolean(AppConstants.IS_LOGGED, false)


    override fun logout() {
        sharedPreferences.edit().apply {
            clear()
            commit()
        }
    }

    override fun getCurrentUser(): UserModel {
        with(sharedPreferences) {
            return UserModel(
                name = getString(AppConstants.NAME, "") ?: "",
                avatar = getString(AppConstants.AVATAR, "") ?: "",
                id = getString(AppConstants.ID, "") ?: "",
                email = getString(AppConstants.EMAIL, "") ?: ""
            )
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