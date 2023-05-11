package com.example.diplom.data.network.chats.model.response

import com.example.diplom.data.network.personinfo.model.response.MyProfileResponse

data class ChatResponse(
    val id: Long,
    val firstUserId: Long,
    val secondUser: MyProfileResponse
)
