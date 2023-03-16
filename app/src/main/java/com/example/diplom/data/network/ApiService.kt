package com.example.diplom.data.network


import com.example.diplom.data.network.model.request.SignUpRequest
import com.example.diplom.data.network.model.response.AuthResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("/signup")
    suspend fun signUp(@Body signUpRequest: SignUpRequest) : Response<AuthResponse>
}