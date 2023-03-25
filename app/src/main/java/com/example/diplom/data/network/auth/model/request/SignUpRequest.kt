package com.example.diplom.data.network.auth.model.request

data class SignUpRequest(
    val email: String,
    val password: String,
    val confirmPassword: String,
    val name: String,
    val surname: String,
    val dateOfBirthday: Long,
    val categoryId: Long
)