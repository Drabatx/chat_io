package com.drabatx.chatio.di


import android.content.Context
import android.content.SharedPreferences
import com.drabatx.chatio.data.domain.repository.AutenticateRepository
import com.drabatx.chatio.data.domain.repository.AutenticateRepositoryImpl
import com.drabatx.chatio.data.domain.repository.ChatRepository
import com.drabatx.chatio.data.domain.repository.ChatRepositoryImpl
import com.drabatx.chatio.data.domain.repository.LoginRepository
import com.drabatx.chatio.data.domain.repository.LoginRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideAutenticateRepository(
        sharedPreferences: SharedPreferences,
        firebaseAuth: FirebaseAuth
    ): AutenticateRepository {
        return AutenticateRepositoryImpl(sharedPreferences, firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideLoginRepository(
        firebaseAuth: FirebaseAuth,
        autenticateRepository: AutenticateRepository, firebaseDatabase: FirebaseDatabase
    ): LoginRepository {
        return LoginRepositoryImpl(
            firebaseAuth,
            autenticateRepository,
            firebaseDatabase = firebaseDatabase
        )
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        firebaseDatabase: FirebaseDatabase,
        autenticateRepository: AutenticateRepository
    ): ChatRepository {
        return ChatRepositoryImpl(firebaseDatabase, autenticateRepository)
    }
}