package com.drabatx.chatio.data.mappers

import com.drabatx.chatio.data.model.UserModel
import com.google.firebase.auth.FirebaseUser

object FirebaseUserToUserModelMapper {
    fun map(firebaseUser: FirebaseUser) = UserModel.Builder()
        .name(firebaseUser.displayName ?: "")
        .email(firebaseUser.email ?: "")
        .avatar(firebaseUser.photoUrl?.toString() ?: "")
        .id(firebaseUser.uid).build()

}