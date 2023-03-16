package com.example.diplom.data.network

import android.util.Log
import com.example.diplom.data.network.model.request.SignUpRequest
import com.example.diplom.data.network.model.response.AuthResponse
import retrofit2.Response

class ApiClient(private val apiService: ApiService){

    suspend fun signUp(signUpRequest: SignUpRequest): SimpleResponse<AuthResponse> =
        safeApiCall { apiService.signUp(signUpRequest) }


    private inline fun <T> safeApiCall(apiCall: () -> Response<T>): SimpleResponse<T> {
        return try {
            SimpleResponse.success(apiCall.invoke())
        } catch (e: Exception) {
            Log.e("error", e.toString())
            SimpleResponse.failure(e)
        }
    }
}