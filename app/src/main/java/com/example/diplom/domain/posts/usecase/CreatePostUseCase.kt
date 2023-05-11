package com.example.diplom.domain.posts.usecase

import com.example.diplom.data.network.posts.model.request.PostRequest
import com.example.diplom.domain.posts.repository.PostRepository

class CreatePostUseCase(
    private val postRepository: PostRepository
) {
    suspend fun execute(post: PostRequest) = postRepository.createPost(post)
}