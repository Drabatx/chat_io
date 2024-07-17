package com.drabatx.chatio.data.model


import com.google.gson.annotations.SerializedName

data class UserModel(
    @SerializedName("displayName")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("photoUrl")
    val avatar: String,
    @SerializedName("uid")
    val id: String
) {
    class Builder {
        private var name: String = ""
        private var email: String = ""
        private var avatar: String = ""
        private var id: String = ""

        fun name(name: String) = apply { this.name = name }
        fun email(email: String) = apply { this.email = email }
        fun avatar(avatar: String) = apply { this.avatar = avatar }
        fun id(id: String) = apply { this.id = id }

        fun build() = UserModel(name, email, avatar, id)
    }
}