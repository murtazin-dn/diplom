package com.example.diplom.presentation.ui.auth.signup.model

data class SignUpFields(
    val email: String,
    val password: String,
    val confirmPassword: String,
    val name: String,
    val surname: String,
    val dateOfBirthday: String,
    val categoryId: Long?
)
