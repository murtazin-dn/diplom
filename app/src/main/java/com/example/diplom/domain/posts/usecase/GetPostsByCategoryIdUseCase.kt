package com.example.diplom.domain.posts.usecase

import com.example.diplom.domain.posts.repository.PostRepository

class GetPostsByCategoryIdUseCase(
    private val postRepository: PostRepository
) {
    suspend fun execute(categoryId: Long) = postRepository.getPostsByCategoryId(categoryId)
}