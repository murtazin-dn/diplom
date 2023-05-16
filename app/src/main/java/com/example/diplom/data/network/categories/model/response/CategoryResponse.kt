package com.example.diplom.data.network.categories.model.response

@kotlinx.serialization.Serializable
data class CategoryResponse(
    val id: Long,
    val name: String
){
    override fun toString() = name
}

