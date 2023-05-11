package com.example.diplom.data.network.personinfo.repository

import com.example.diplom.data.network.personinfo.model.response.ProfileResponse
import com.example.diplom.data.network.personinfo.model.response.MyProfileResponse
import com.example.diplom.data.network.personinfo.model.response.UserPhotoResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface PersonInfoService {
    @GET("users/info")
    suspend fun getMyProfile(
        @Header("Authorization") token: String):
            Response<MyProfileResponse>

    @GET("users/info/{userId}")
    suspend fun getProfile(
        @Header("Authorization") token: String,
        @Path("userId") userId: Long):
            Response<ProfileResponse>

    @GET("users")
    suspend fun findUsers(
        @Header("Authorization") token: String,
        @Query("text") text: String):
            Response<List<ProfileResponse>>

    @Multipart
    @POST("users/photo")
    suspend fun uploadUserPhoto(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Response<UserPhotoResponse>
}