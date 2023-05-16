package com.example.diplom.domain.personinfo.usecase

import android.net.Uri
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.personinfo.repository.PersonInfoRepository
import kotlinx.coroutines.flow.Flow
import java.io.File

class UploadPhotoUseCase(
    private val personInfoRepository: PersonInfoRepository
) {
    suspend fun execute(file: File): Flow<Response<String>> =
        personInfoRepository.uploadPhoto(file)
}