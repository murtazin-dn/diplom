package com.example.diplom.data.network.personinfo.model.response

import com.example.diplom.data.network.categories.model.response.CategoryResponse

@kotlinx.serialization.Serializable
data class MyProfileResponse(
    val id: Long,
    val name: String,
    val surname: String,
    val category: CategoryResponse,
    val doctorStatus: Boolean,
    val dateOfBirthday: Long,
    val icon: String?
)
