package com.example.diplom.data.network.personinfo.repository

import com.example.diplom.data.network.personinfo.model.response.ProfileResponse
import com.example.diplom.data.network.personinfo.model.response.MyProfileResponse
import com.example.diplom.data.network.util.FileIsMissingException
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.personinfo.repository.PersonInfoRepository
import com.example.diplom.util.TokenService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class ProfileRepositoryImpl(
    private val service: PersonInfoService,
    private val tokenService: TokenService
): PersonInfoRepository {
    override suspend fun getMyProfile(): Flow<Response<MyProfileResponse>> {
        return flow {
            val token = tokenService.getToken()
            val response = service.getMyProfile("Bearer $token")
            if(response.isSuccessful){
                val data = response.body()!!
                emit(Response.Success(data))
            }else{
                emit(Response.Error(response.code(), response.errorBody().toString()))
            }
        }
    }

    override suspend fun getProfile(userId: Long): Flow<Response<ProfileResponse>> {
        return flow {
            val token = tokenService.getToken()
            val response = service.getProfile("Bearer $token", userId)
            if(response.isSuccessful){
                val data = response.body()!!
                emit(Response.Success(data))
            }else{
                emit(Response.Error(response.code(), response.errorBody().toString()))
            }
        }
    }

    override suspend fun findUsers(text: String): Flow<Response<List<ProfileResponse>>> {
        return flow {
            val token = tokenService.getToken()
            val response = service.findUsers("Bearer $token", text)
            if(response.isSuccessful){
                val data = response.body()!!
                emit(Response.Success(data))
            }else{
                emit(Response.Error(response.code(), response.errorBody().toString()))
            }
        }
    }

    override suspend fun uploadUserPhoto(file: File): Flow<Response<String>> {
        return flow{
            val token = tokenService.getToken()
            if (file.exists()) {
                val filePart = createFormData(
                    "file",
                    file.name,
                    file.asRequestBody()
                )
                val response = service.uploadUserPhoto("Bearer $token", filePart)
                if(response.isSuccessful){
                    val data = response.body()!!
                    emit(Response.Success(data.name))
                }else{
                    emit(Response.Error(response.code(), response.errorBody().toString()))
                }
            }
            else{
                throw FileIsMissingException()
            }
        }
    }
}