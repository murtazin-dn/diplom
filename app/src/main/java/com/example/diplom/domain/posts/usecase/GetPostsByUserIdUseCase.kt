package com.example.diplom.domain.posts.usecase

import com.example.diplom.data.network.posts.model.response.PostResponse
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.posts.repository.PostRepository
import kotlinx.coroutines.flow.Flow

class GetPostsByUserIdUseCase(
    private val postRepository: PostRepository
) {
    suspend fun execute(userId: Long): Flow<Response<List<PostResponse>>> =
        postRepository.getPostsByUserId(userId)
}