package com.example.diplom.data.network.auth.repository

import com.example.diplom.data.network.auth.model.request.SignInRequest
import com.example.diplom.data.network.auth.model.request.SignUpRequest
import com.example.diplom.data.network.auth.model.response.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthService {
    @POST("signup")
    suspend fun signUp(@Body signUpRequest: SignUpRequest) : Response<AuthResponse>

    @POST("signin")
    suspend fun signIn(@Body signInRequest: SignInRequest) : Response<AuthResponse>
}