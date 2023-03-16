package com.example.diplom.di

import com.example.diplom.BuildConfig
import com.example.diplom.data.network.ApiClient
import com.example.diplom.data.network.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


val networkModule = module {
    single { provideOkHttpClient() }
    single { provideRetrofit( okHttpClient = get(), BuildConfig.BASE_URL) }
    single { provideApiService( retrofit = get()) }
    single { provideApiClient(apiService = get()) }
}

private fun provideApiClient(apiService: ApiService) = ApiClient(apiService)

private fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
} else OkHttpClient
    .Builder()
    .build()

private fun provideRetrofit(
    okHttpClient: OkHttpClient,
    BASE_URL: String
): Retrofit =
    Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

private fun provideApiService(retrofit: Retrofit): ApiService =
    retrofit.create(ApiService::class.java)