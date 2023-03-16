package com.example.diplom.domain.model

data class SignUp(
    val email: String,
    val password: String,
    val confirmPassword: String,
    val name: String,
    val surname: String,
    val dateOfBirthday: Long,
    val categoryId: Long
)
