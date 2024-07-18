package com.drabatx.chatio.data.domain.repository

import com.drabatx.chatio.data.model.ChatModel
import com.drabatx.chatio.data.model.MessageModel
import com.drabatx.chatio.di.AppConstants.CHAT_TABLE
import com.drabatx.chatio.utils.Result
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
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

            val receiveAllMessages =
                suspendCancellableCoroutine<Result<ChatModel>> { continuation ->
                    chatRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val messageList = snapshot.children.mapNotNull { childSnapshot ->
                                val text =
                                    childSnapshot.child("text").getValue(String::class.java) ?: ""
                                val sender =
                                    childSnapshot.child("sender").getValue(String::class.java) ?: ""
                                val timestamp =
                                    childSnapshot.child("timestamp").getValue(Long::class.java)
                                        ?: 0L
                                val imageUrl =
                                    childSnapshot.child("imageUrl").getValue(String::class.java)
                                        ?: ""

                                MessageModel.Builder()
                                    .setText(text)
                                    .setSender(sender)
                                    .setTimestamp(timestamp)
                                    .setImageUrl(imageUrl)
                                    .build()
                            }
                            continuation.resume(Result.Success(ChatModel(emptyList(), messageList)))
                        }

                        override fun onCancelled(error: DatabaseError) {
                            continuation.resumeWithException(Throwable("Error al realizar la operación: ${error.message}"))
                        }
                    })

                }
            emit(receiveAllMessages)
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}