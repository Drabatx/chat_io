package com.drabatx.chatio.di


import android.content.Context
import android.content.SharedPreferences
import com.drabatx.chatio.data.domain.repository.AutenticateRepository
import com.drabatx.chatio.data.domain.repository.AutenticateRepositoryImpl
import com.drabatx.chatio.data.domain.repository.LoginRepository
import com.drabatx.chatio.data.domain.repository.LoginRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(Interceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("accept", "application/json")
                val request = requestBuilder.build()
                chain.proceed(request)
            })
            .build()
        return Retrofit.Builder().baseUrl(NetworkConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideAutenticateRepository(sharedPreferences: SharedPreferences): AutenticateRepository {
        return AutenticateRepositoryImpl(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideLoginRepository(
        firebaseAuth: FirebaseAuth,
        autenticateRepository: AutenticateRepository
    ): LoginRepository {
        return LoginRepositoryImpl(firebaseAuth, autenticateRepository)
    }
}