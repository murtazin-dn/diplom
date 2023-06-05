package com.example.diplom.data.network.chats.model.response

import com.example.diplom.data.network.messages.model.response.MessageResponse

data class ChatPreview(
    val chatId: Long,
    val userId: Long,
    val name: String,
    val surname: String,
    val icon: String?,
    val unreadMessagesCount: Long,
    val lastMessage: MessageResponse
)
