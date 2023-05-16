package com.example.diplom.di

import com.example.diplom.BuildConfig
import com.example.diplom.data.network.auth.repository.AuthService
import com.example.diplom.data.network.categories.repository.CategoryService
import com.example.diplom.data.network.chats.repository.ChatService
import com.example.diplom.data.network.messages.repository.MessagesService
import com.example.diplom.data.network.notifications.repository.NotificationsService
import com.example.diplom.data.network.personinfo.repository.PersonInfoService
import com.example.diplom.data.network.posts.repository.PostService
import com.example.diplom.data.network.subscribers.repository.SubscribersService
import com.example.diplom.util.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


val networkModule = module {
    single { provideOkHttpClient() }
    single { provideRetrofit( okHttpClient = get()) }
    single { provideAuthService( retrofit = get()) }
    single { provideCategoryService( retrofit = get()) }
    single { providePersonInfoService( retrofit = get()) }
    single { providePostService( retrofit = get()) }
    single { provideChatService( retrofit = get()) }
    single { provideMessagesService( retrofit = get()) }
    single { provideSubscribersService(retrofit = get()) }
    single { provideNotificationsService(retrofit = get()) }
}
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
    okHttpClient: OkHttpClient
): Retrofit =
    Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(
            Moshi.Builder().addLast(KotlinJsonAdapterFactory()
        ).build()))
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

private fun provideAuthService(retrofit: Retrofit): AuthService =
    retrofit.create(AuthService::class.java)

private fun provideCategoryService(retrofit: Retrofit): CategoryService =
    retrofit.create(CategoryService::class.java)

private fun providePersonInfoService(retrofit: Retrofit): PersonInfoService =
    retrofit.create(PersonInfoService::class.java)

private fun providePostService(retrofit: Retrofit): PostService =
    retrofit.create(PostService::class.java)

private fun provideChatService(retrofit: Retrofit): ChatService =
    retrofit.create(ChatService::class.java)

private fun provideMessagesService(retrofit: Retrofit): MessagesService =
    retrofit.create(MessagesService::class.java)

private fun provideSubscribersService(retrofit: Retrofit): SubscribersService =
    retrofit.create(SubscribersService::class.java)

private fun provideNotificationsService(retrofit: Retrofit): NotificationsService =
    retrofit.create(NotificationsService::class.java)