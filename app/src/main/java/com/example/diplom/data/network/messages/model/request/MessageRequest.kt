package com.example.diplom.data.network.messages.model.request

@kotlinx.serialization.Serializable
data class MessageRequest(
    val text: String,
    var images: List<String>
)
