package com.drabatx.chatio.data.domain.repository

import android.content.SharedPreferences
import com.drabatx.chatio.di.NetworkConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AutenticateRepositoryImpl(private val sharedPreferences: SharedPreferences) :
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
}