package com.drabatx.chatio.data.model

data class MessageModel(
    val text: String,
    val sender: String,
    val timestamp: Long,
    val imageUrl: String,
    val user: SenderModel? = null,
    val isThisUser: Boolean = false
) {
    class Builder {
        private var text: String = ""
        private var sender: String = ""
        private var timestamp: Long = 0
        private var imageUrl: String = ""
        private var userModel: SenderModel? = null
        private var isThisUser: Boolean = false
        fun setText(text: String) = apply { this.text = text }
        fun setSender(sender: String) = apply { this.sender = sender }
        fun setTimestamp(timestamp: Long) = apply { this.timestamp = timestamp }
        fun setImageUrl(imageUrl: String) = apply { this.imageUrl = imageUrl }
        fun setUser(userModel: SenderModel) = apply { this.userModel = userModel }
        fun setIsThisUser(isThisUser: Boolean) = apply { this.isThisUser = isThisUser }

        fun build() = MessageModel(text, sender, timestamp, imageUrl)
    }

    // Optionally, you can define a companion object for easier usage
    companion object {
        fun builder() = Builder()
    }
}