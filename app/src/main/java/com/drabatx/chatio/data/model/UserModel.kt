package com.drabatx.chatio.data.model


data class UserModel(
    val name: String? = "",
    val email: String? = "",
    val avatar: String? = "",
    val id: String? = ""
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