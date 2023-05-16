package com.example.diplom.di

import com.example.diplom.data.network.personinfo.repository.ProfileRepositoryImpl
import com.example.diplom.domain.personinfo.repository.PersonInfoRepository
import com.example.diplom.domain.personinfo.usecase.*
import com.example.diplom.presentation.ui.createpost.PhotosAdapter
import org.koin.dsl.module

val personInfoModule = module {
    single<PersonInfoRepository> { ProfileRepositoryImpl(service = get(), tokenService = get()) }
    single { GetMyProfileUseCase(repository = get()) }
    single { GetProfileUseCase(repository = get()) }
    single { UploadUserPhotoUseCase( personInfoRepository = get())}
    single { UploadPhotoUseCase( personInfoRepository = get()) }
    single { FindUsersUseCase( personInfoRepository = get())}
    factory { PhotosAdapter( uploadPhotoUseCase = get()) }
}