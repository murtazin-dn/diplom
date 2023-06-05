package com.example.diplom.data.network.messages.model.response

@kotlinx.serialization.Serializable
data class MessageResponse(
    val id: Long,
    val chatId: Long,
    val userId: Long,
    val text: String,
    val date: Long,
    val type: MessageType,
    val isRead: Boolean,
    val images: MutableList<String>
)
enum class MessageType {
    IN, OUT
}
