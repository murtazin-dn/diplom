package com.example.diplom.domain.personinfo.usecase

import com.example.diplom.domain.personinfo.repository.PersonInfoRepository

class FindUsersUseCase(
    private val personInfoRepository: PersonInfoRepository
) {
    suspend fun execute(text: String) = personInfoRepository.findUsers(text)
}