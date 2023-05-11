package com.example.diplom.di

import com.example.diplom.data.network.posts.repository.PostRepositoryImpl
import com.example.diplom.domain.posts.repository.PostRepository
import com.example.diplom.domain.posts.usecase.*
import org.koin.dsl.module

val postsModule = module {
    single<PostRepository> { PostRepositoryImpl(postService = get(), tokenService = get()) }
    single { GetMySubscriptionsPostUseCase(postRepository = get()) }
    single { GetPostsByUserIdUseCase(postRepository = get()) }
    single { GetPostsByCategoryIdUseCase(postRepository = get()) }
    single { GetAllPostsUseCase(postRepository = get()) }
    single { GetPostByIdUseCase(postRepository = get()) }
    single { SetLikeUseCase(postRepository = get()) }
    single { UnsetLikeUseCase(postRepository = get()) }
    single { GetCommentsUseCase(postRepository = get()) }
    single { CreateCommentUseCase(postRepository = get()) }
    single { CreatePostUseCase(postRepository = get()) }
}