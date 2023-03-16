package com.example.diplom.data.network.model.request

data class SignUpRequest(
    val email: String,
    val password: String,
    val confirmPassword: String,
    val name: String,
    val surname: String,
    val dateOfBirthday: Long,
    val categoryId: Long
)