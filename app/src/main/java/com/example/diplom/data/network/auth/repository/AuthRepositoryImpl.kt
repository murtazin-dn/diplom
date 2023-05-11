package com.example.diplom.data.network.auth.repository

import com.example.diplom.data.network.auth.model.request.SignInRequest
import com.example.diplom.data.network.auth.model.request.SignUpRequest
import com.example.diplom.data.network.auth.model.response.AuthResponse
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.auth.repository.AuthRepository
import com.example.diplom.util.TokenService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
    private val authService: AuthService,
    private val tokenService: TokenService
    ): AuthRepository {

    override suspend fun signUp(signUpRequest: SignUpRequest): Flow<Response<AuthResponse>> {
        return flow {
            val response = authService.signUp(signUpRequest)
            if (response.isSuccessful){
                val body = response.body()!!
                tokenService.saveUserId(body.userId)
                tokenService.saveToken(body.token)
                emit(Response.Success(body))
            }else{
                emit(Response.Error(response.code(), response.errorBody()?.charStream()?.readText()!!))
            }
        }
    }

    override suspend fun signIn(signInRequest: SignInRequest): Flow<Response<AuthResponse>> {
        return flow {
            val response = authService.signIn(signInRequest)
            if (response.isSuccessful){
                val body = response.body()!!
                tokenService.saveUserId(body.userId)
                tokenService.saveToken(body.token)
                emit(Response.Success(body))
            }else{
                emit(Response.Error(response.code(), response.errorBody()?.charStream()?.readText()!!))
            }
        }
    }

    override suspend fun isTakenEmail(email: String): Flow<Response<Boolean>> {
        return flow{
            val response = authService.isTakenEmail(email)
            if (response.isSuccessful){
                val body = response.body()!!
                emit(Response.Success(body.isTaken))
            }else{
                emit(Response.Error(response.code(), response.errorBody()?.charStream()?.readText()!!))
            }
        }
    }
}