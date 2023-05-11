package com.example.diplom.domain.personinfo.usecase

import com.example.diplom.data.network.personinfo.model.response.MyProfileResponse
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.personinfo.repository.PersonInfoRepository
import kotlinx.coroutines.flow.Flow

class GetMyProfileUseCase(
    private val repository: PersonInfoRepository
) {
    suspend fun execute(): Flow<Response<MyProfileResponse>> =
        repository.getMyProfile()
}