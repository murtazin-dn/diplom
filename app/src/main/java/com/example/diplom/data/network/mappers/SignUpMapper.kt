package com.example.diplom.data.network.mappers

import com.example.diplom.data.network.model.request.SignUpRequest
import com.example.diplom.domain.model.SignUp

fun SignUp.toSignUpRequest() = SignUpRequest(
    email = this.email,
    password = this.password,
    confirmPassword = this.confirmPassword,
    name = this.name,
    surname = this.surname,
    dateOfBirthday = this.dateOfBirthday,
    categoryId = this.categoryId

)