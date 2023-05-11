package com.example.diplom.domain.posts.usecase

import com.example.diplom.domain.posts.repository.PostRepository

class SetLikeUseCase(
    private val postRepository: PostRepository
) {
    suspend fun execute(postId: Long) = postRepository.setLike(postId)
}