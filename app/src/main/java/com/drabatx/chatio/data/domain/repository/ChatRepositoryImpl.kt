package com.drabatx.chatio.data.domain.repository

import android.net.Uri
import com.drabatx.chatio.data.model.ChatModel
import com.drabatx.chatio.data.model.MessageModel
import com.drabatx.chatio.data.model.SenderModel
import com.drabatx.chatio.di.AppConstants.CHAT_TABLE
import com.drabatx.chatio.di.AppConstants.USER_TABLE
import com.drabatx.chatio.utils.Result
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ChatRepositoryImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val autenticateRepository: AutenticateRepository
) :
    ChatRepository {
    override suspend fun sendMessage(message: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            val chatRef = firebaseDatabase.getReference(CHAT_TABLE)
            val messageKey = chatRef.push().key
                ?: throw NullPointerException("No se pudo obtener la clave de la referencia")
            val messageModel = MessageModel.builder().setText(message)
                .setSender(autenticateRepository.getCurrentUser().id ?: "")
                .setTimestamp(System.currentTimeMillis()).build()

            val sendMessageCourotine = suspendCancellableCoroutine<Result<Unit>> { continuation ->
                chatRef.child(messageKey).setValue(messageModel).addOnSuccessListener {
                    continuation.resume(Result.Success(Unit))
                }.addOnFailureListener {
                    continuation.resumeWithException(it)
                }
            }
            emit(sendMessageCourotine)
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override suspend fun getAllMessages(): Flow<Result<ChatModel>> = flow {
        emit(Result.Loading)
        try {
            val chatRef = firebaseDatabase.getReference(CHAT_TABLE)

            val messageFlow = MutableStateFlow<Result<ChatModel>>(Result.Loading)
            // Escuchar cambios en el nodo "chats"
            chatRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messageModels = mutableListOf<MessageModel>()

                    // Iterar sobre los mensajes en "chats"
                    snapshot.children.forEach { chatSnapshot ->
                        val text = chatSnapshot.child("text").getValue(String::class.java) ?: ""
                        val sender = chatSnapshot.child("sender").getValue(String::class.java) ?: ""
                        val timestamp =
                            chatSnapshot.child("timestamp").getValue(Long::class.java) ?: 0L
                        val imageUrl =
                            chatSnapshot.child("imageUrl").getValue(String::class.java) ?: ""

                        // Obtener detalles del usuario (en una coroutine)
                        firebaseDatabase.getReference(USER_TABLE).child(sender).get()
                            .addOnSuccessListener { userSnapshot ->
                                val name =
                                    userSnapshot.child("name").getValue(String::class.java) ?: ""
                                val email =
                                    userSnapshot.child("email").getValue(String::class.java) ?: ""
                                val avatar =
                                    userSnapshot.child("avatar").getValue(String::class.java) ?: ""
                                val id = userSnapshot.child("id").getValue(String::class.java) ?: ""
                                val senderModel = SenderModel(id, email, name, avatar)

                                val messageModel = MessageModel.builder()
                                    .setText(text)
                                    .setSender(sender)
                                    .setTimestamp(timestamp)
                                    .setImageUrl(imageUrl)
                                    .setUser(senderModel)
                                    .setIsThisUser(sender == autenticateRepository.getCurrentUser().id)
                                    .build()

                                messageModels.add(messageModel)

                                // Emitir el resultado exitoso con los mensajes actualizados
                                if (messageModels.size == snapshot.childrenCount.toInt()) {
                                    val messages = messageModels
                                    messageFlow.value =
                                        Result.Success(ChatModel(emptyList(), messages))
                                }
                            }.addOnFailureListener { exception ->
                                // Manejar errores de Firebase
                                messageFlow.value =
                                    Result.Error(Exception("Firebase ValueEventListener cancelled"))
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejar errores de cancelación
                    messageFlow.value =
                        Result.Error(Exception("Firebase ValueEventListener cancelled"))
                }
            })
            emitAll(messageFlow)

        } catch (e: Exception) {
            // Manejar errores generales
            emit(Result.Error(e))
        }
    }

    override suspend fun sendImage(imageUri: Uri): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            val senImageState = MutableStateFlow<Result<Unit>>(Result.Loading)
            val imageName = "${UUID.randomUUID()}.jpg"
            val storage = Firebase.storage
            val storageRef = storage.reference.child("images/chat/$imageName")
            val uploadTask = storageRef.putFile(imageUri)
            uploadTask.addOnFailureListener { error ->
                senImageState.value = Result.Error(error)
            }.addOnProgressListener {
                senImageState.value = Result.Loading
            }.addOnSuccessListener { snapshot ->
                try {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val chatRef = firebaseDatabase.getReference(CHAT_TABLE)
                        val messageKey = chatRef.push().key
                            ?: throw NullPointerException("No se pudo obtener la clave de la referencia")

                        val messageModel = MessageModel.builder().setText("")
                            .setSender(autenticateRepository.getCurrentUser().id ?: "")
                            .setImageUrl(uri.toString())
                            .setTimestamp(System.currentTimeMillis()).build()
                        chatRef.child(messageKey).setValue(messageModel).addOnSuccessListener {
                            senImageState.value = Result.Success(Unit)
                        }.addOnFailureListener {
                            senImageState.value = Result.Error(it)
                        }
                    }

                } catch (ex: Exception) {
                    senImageState.value = Result.Error(ex)
                }
            }
            emitAll(senImageState)
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}