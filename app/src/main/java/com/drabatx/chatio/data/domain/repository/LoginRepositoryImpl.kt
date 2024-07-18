package com.drabatx.chatio.data.domain.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.drabatx.chatio.data.model.UserModel
import com.drabatx.chatio.data.model.response.LoginResponse
import com.drabatx.chatio.di.AppConstants.USER_TABLE
import com.drabatx.chatio.utils.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LoginRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val autenticateRepository: AutenticateRepository,
    private val firebaseDatabase: FirebaseDatabase
) :
    LoginRepository {

    private val _registerUser = MutableLiveData<Result<LoginResponse>>()
    val registerUser: LiveData<Result<LoginResponse>> get() = _registerUser

    override suspend fun login(userName: String, password: String): Flow<Result<LoginResponse>> =
        flow {
            emit(Result.Loading)
            try {
                val authResult =
                    firebaseAuth.signInWithEmailAndPassword(userName, password)
                        .await()
                val firebaseUser =
                    authResult.user ?: throw NullPointerException("El usuario no se pudo crear")
                val usersRef = firebaseDatabase.getReference(USER_TABLE).child(firebaseUser.uid)

                val loginUser = suspendCancellableCoroutine { continuation ->
                    usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val userModel = snapshot.getValue(UserModel::class.java)
                                if (userModel != null) {
                                    autenticateRepository.startSession(userModel, true)
                                    continuation.resume(
                                        Result.Success(
                                            LoginResponse(
                                                "Usuario obtenido",
                                                userModel
                                            )
                                        )
                                    )
                                } else {
                                    continuation.resume(Result.Error(Throwable("Error al obtener el usuario")))
                                }
                            } else {
                                continuation.resume(Result.Error(Throwable("El usuario no existe")))
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            continuation.resumeWithException(Throwable("Error al realizar la operación: ${error.message}"))
                        }
                    })
                }
                emit(loginUser)
            } catch (e: Exception) {
                if (e is FirebaseAuthInvalidCredentialsException) {
                    emit(Result.Error(Throwable("La credencial de autenticación proporcionada es incorrecta, tiene un formato incorrecto o ha caducado.")))
                } else {
                    emit(Result.Error(e))
                }
            }
        }

    override suspend fun register(
        userName: String,
        password: String
    ): Flow<Result<LoginResponse>> = flow {
        emit(Result.Loading)
        try {
            val authResult =
                firebaseAuth.createUserWithEmailAndPassword(userName, password)
                    .await()
            val firebaseUser =
                authResult.user ?: throw NullPointerException("El usuario no se pudo crear")
            val userModel = UserModel(
                name = userName,
                email = userName,
                avatar = "",
                id = firebaseUser.uid
            )
            val usersRef = firebaseDatabase.getReference(USER_TABLE).child(firebaseUser.uid)
            val registerUser = suspendCancellableCoroutine<Result<LoginResponse>> { continuation ->
                usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists()) {
                            usersRef.setValue(userModel).addOnSuccessListener {
                                autenticateRepository.startSession(userModel, true)
                                continuation.resume(
                                    Result.Success(
                                        LoginResponse(
                                            "Usuario registrado",
                                            userModel
                                        )
                                    )
                                )
                            }.addOnFailureListener { exception ->
                                continuation.resumeWithException(exception)
                            }
                        } else {
                            continuation.resumeWithException(Throwable("El usuario ya existe"))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWithException(Throwable("Error al realizar la operación"))
                    }
                })
            }
            emit(registerUser)
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}