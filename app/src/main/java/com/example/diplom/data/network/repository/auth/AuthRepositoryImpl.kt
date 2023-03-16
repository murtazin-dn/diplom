package com.example.diplom.data.network.repository.auth

import com.example.diplom.data.network.ApiClient
import com.example.diplom.data.network.SimpleResponse
import com.example.diplom.data.network.model.request.SignUpRequest
import com.example.diplom.data.network.model.response.AuthResponse
import com.example.diplom.domain.repository.AuthRepository
import com.example.diplom.util.TokenService

class AuthRepositoryImpl(
    private val apiClient: ApiClient,
    private val tokenService: TokenService
    ): AuthRepository {

    override suspend fun signUp(signUpRequest: SignUpRequest): SimpleResponse<AuthResponse>{
        val response = apiClient.signUp(signUpRequest)
        if(response.isSuccessful){
            saveToken(response.body.token)
        }
        return response
    }

    private fun saveToken(token: String) {
        tokenService.saveToken(token)
    }
}