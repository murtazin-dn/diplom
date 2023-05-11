package com.example.diplom.domain.posts.usecase

import com.example.diplom.data.network.posts.model.request.CommentRequest
import com.example.diplom.domain.posts.repository.PostRepository

class CreateCommentUseCase(
    private val postRepository: PostRepository
) {
    suspend fun execute(postId: Long, comment: CommentRequest) =
        postRepository.createComment(postId, comment)
}