package com.example.diplom.domain.personinfo.repository

import com.example.diplom.data.network.personinfo.model.response.ProfileResponse
import com.example.diplom.data.network.personinfo.model.response.MyProfileResponse
import com.example.diplom.data.network.util.Response
import kotlinx.coroutines.flow.Flow
import java.io.File

interface PersonInfoRepository {
    suspend fun getMyProfile(): Flow<Response<MyProfileResponse>>
    suspend fun getProfile(userId: Long): Flow<Response<ProfileResponse>>
    suspend fun findUsers(text: String): Flow<Response<List<ProfileResponse>>>
    suspend fun uploadUserPhoto(file: File): Flow<Response<String>>
}