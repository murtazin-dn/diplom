package com.example.diplom.presentation.services.model

import com.example.diplom.data.network.chats.model.response.ChatResponse
import com.example.diplom.data.network.personinfo.model.response.MyProfileResponse
import kotlinx.serialization.Serializable

@Serializable
data class NotificationMessage(
    val id: Long,
    val chat: ChatResponse,
    val user: MyProfileResponse,
    val text: String,
    val date: Long
)