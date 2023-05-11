package com.example.diplom.domain.posts.usecase

import com.example.diplom.domain.posts.repository.PostRepository

class GetCommentsUseCase(
    private val postRepository: PostRepository
) {
    suspend fun execute(postId: Long) = postRepository.getComments(postId)
}