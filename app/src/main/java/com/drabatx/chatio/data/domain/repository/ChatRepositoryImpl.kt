package com.drabatx.chatio.data.domain.repository

import com.drabatx.chatio.data.model.ChatModel
import com.drabatx.chatio.data.model.MessageModel
import com.drabatx.chatio.data.model.SenderModel
import com.drabatx.chatio.data.model.UserModel
import com.drabatx.chatio.di.AppConstants.CHAT_TABLE
import com.drabatx.chatio.di.AppConstants.USER_TABLE
import com.drabatx.chatio.utils.Result
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

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
        val messageModels = mutableListOf<MessageModel>()
        try {
            val chatRef = firebaseDatabase.getReference(CHAT_TABLE)
            val userRef = firebaseDatabase.getReference(USER_TABLE)
            val snapshot = chatRef.get().await()
            snapshot.children.forEach { chatSnapshot ->

                val text = chatSnapshot.child("text").getValue(String::class.java) ?: ""
                val sender = chatSnapshot.child("sender").getValue(String::class.java) ?: ""
                val timestamp = chatSnapshot.child("timestamp").getValue(Long::class.java) ?: 0L
                val imageUrl = chatSnapshot.child("imageUrl").getValue(String::class.java) ?: ""

                val userSnapshot = userRef.child(sender).get().await()
                val name = userSnapshot.child("name").getValue(String::class.java) ?: ""
                val email = userSnapshot.child("email").getValue(String::class.java) ?: ""
                val avatar = userSnapshot.child("avatar").getValue(String::class.java) ?: ""
                val id = userSnapshot.child("id").getValue(String::class.java) ?: ""
                val senderModel = SenderModel(id, email, name, avatar)
                val messageModel =
                    MessageModel.builder()
                        .setText(text)
                        .setSender(sender)
                        .setTimestamp(timestamp)
                        .setImageUrl(imageUrl)
                        .setUser(senderModel)
                        .setIsThisUser(sender == autenticateRepository.getCurrentUser().id)
                        .build()
                messageModels.add(messageModel)
            }
            emit(Result.Success(ChatModel(emptyList(), messageModels.reversed())))

        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

}