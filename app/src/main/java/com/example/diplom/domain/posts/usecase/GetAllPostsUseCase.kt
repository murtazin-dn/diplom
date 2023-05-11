package com.example.diplom.domain.posts.usecase

import com.example.diplom.domain.posts.repository.PostRepository

class GetAllPostsUseCase(
    private val postRepository: PostRepository
) {
    suspend fun execute() = postRepository.getAllPosts()
}