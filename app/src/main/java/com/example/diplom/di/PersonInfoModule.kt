package com.example.diplom.di

import com.example.diplom.data.network.personinfo.repository.ProfileRepositoryImpl
import com.example.diplom.domain.personinfo.repository.PersonInfoRepository
import com.example.diplom.domain.personinfo.usecase.FindUsersUseCase
import com.example.diplom.domain.personinfo.usecase.GetMyProfileUseCase
import com.example.diplom.domain.personinfo.usecase.GetProfileUseCase
import com.example.diplom.domain.personinfo.usecase.UploadUserPhotoUseCase
import org.koin.dsl.module

val personInfoModule = module {
    single<PersonInfoRepository> { ProfileRepositoryImpl(service = get(), tokenService = get()) }
    single { GetMyProfileUseCase(repository = get()) }
    single { GetProfileUseCase(repository = get()) }
    single { UploadUserPhotoUseCase( personInfoRepository = get())}
    single { FindUsersUseCase( personInfoRepository = get())}
}