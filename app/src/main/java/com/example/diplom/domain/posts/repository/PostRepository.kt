package com.example.diplom.domain.posts.repository

import com.example.diplom.data.network.posts.model.request.CommentRequest
import com.example.diplom.data.network.posts.model.request.PostRequest
import com.example.diplom.data.network.posts.model.response.CommentResponse
import com.example.diplom.data.network.posts.model.response.PostResponse
import com.example.diplom.data.network.util.Response
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    suspend fun getMySubscriptionsPosts(): Flow<Response<List<PostResponse>>>

    suspend fun getPostsByUserId(userId: Long): Flow<Response<List<PostResponse>>>

    suspend fun getPostsByCategoryId(categoryId: Long): Flow<Response<List<PostResponse>>>

    suspend fun getAllPosts(): Flow<Response<List<PostResponse>>>

    suspend fun getPostById(postId: Long): Flow<Response<PostResponse>>

    suspend fun setLike(postId: Long): Flow<Response<Unit>>

    suspend fun unsetLike(postId: Long): Flow<Response<Unit>>

    suspend fun getComments(postId: Long): Flow<Response<List<CommentResponse>>>

    suspend fun createComment(postId: Long, comment: CommentRequest): Flow<Response<Unit>>

    suspend fun createPost(post: PostRequest): Flow<Response<Unit>>
}