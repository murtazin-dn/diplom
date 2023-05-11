package com.example.diplom.domain.personinfo.usecase

import com.example.diplom.data.network.personinfo.model.response.ProfileResponse
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.personinfo.repository.PersonInfoRepository
import kotlinx.coroutines.flow.Flow

class GetProfileUseCase(
    private val repository: PersonInfoRepository
) {
    suspend fun execute(userId: Long): Flow<Response<ProfileResponse>> =
        repository.getProfile(userId)
}