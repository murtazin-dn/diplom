package com.example.diplom.data.network.auth.repository

import com.example.diplom.data.network.auth.model.request.SignInRequest
import com.example.diplom.data.network.auth.model.request.SignUpRequest
import com.example.diplom.data.network.auth.model.response.AuthResponse
import com.example.diplom.data.network.auth.model.response.IsTakenEmail
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthService {
    @POST("signup")
    suspend fun signUp(@Body signUpRequest: SignUpRequest) : Response<AuthResponse>

    @POST("signin")
    suspend fun signIn(@Body signInRequest: SignInRequest) : Response<AuthResponse>

    @GET("email/{email}")
    suspend fun isTakenEmail(@Path("email") email: String) : Response<IsTakenEmail>
}