package com.example.diplom.data.network.posts.repository

import com.example.diplom.data.network.posts.model.request.CommentRequest
import com.example.diplom.data.network.posts.model.request.PostRequest
import com.example.diplom.data.network.posts.model.response.CommentResponse
import com.example.diplom.data.network.posts.model.response.PostResponse
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.posts.repository.PostRepository
import com.example.diplom.util.TokenService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PostRepositoryImpl(
    private val postService: PostService,
    private val tokenService: TokenService
): PostRepository {
    override suspend fun getMySubscriptionsPosts(): Flow<Response<List<PostResponse>>> {
        return flow{
            val token = tokenService.getToken()
            val response = postService.getMySubscriptionsPosts("Bearer $token")
            if (response.isSuccessful){
                emit(Response.Success(response.body()!!))
            }else{
                emit(Response.Error(response.code(), response.errorBody().toString()))
            }
        }
    }

    override suspend fun getPostsByUserId(userId: Long): Flow<Response<List<PostResponse>>> {
        return flow{
            val token = tokenService.getToken()
            val response = postService.getPostsByUserId("Bearer $token", userId)
            if (response.isSuccessful){
                emit(Response.Success(response.body()!!))
            }else{
                emit(Response.Error(response.code(), response.errorBody().toString()))
            }
        }
    }

    override suspend fun getPostsByCategoryId(categoryId: Long): Flow<Response<List<PostResponse>>> {
        return flow{
            val token = tokenService.getToken()
            val response = postService.getPostsByCategoryId("Bearer $token", categoryId)
            if (response.isSuccessful){
                emit(Response.Success(response.body()!!))
            }else{
                emit(Response.Error(response.code(), response.errorBody().toString()))
            }
        }
    }

    override suspend fun getAllPosts(): Flow<Response<List<PostResponse>>> {
        return flow{
            val token = tokenService.getToken()
            val response = postService.getAllPosts("Bearer $token", true)
            if (response.isSuccessful){
                emit(Response.Success(response.body()!!))
            }else{
                emit(Response.Error(response.code(), response.errorBody().toString()))
            }
        }
    }

    override suspend fun getPostById(postId: Long): Flow<Response<PostResponse>> {
        return flow{
            val token = tokenService.getToken()
            val response = postService.getPostById("Bearer $token", postId)
            if (response.isSuccessful){
                emit(Response.Success(response.body()!!))
            }else{
                emit(Response.Error(response.code(), response.errorBody().toString()))
            }
        }
    }

    override suspend fun setLike(postId: Long): Flow<Response<Unit>> {
        return flow{
            val token = tokenService.getToken()
            val response = postService.setLike("Bearer $token", postId)
            if (response.isSuccessful){
                emit(Response.Success(Unit))
            }else{
                emit(Response.Error(response.code(), response.errorBody().toString()))
            }
        }
    }

    override suspend fun unsetLike(postId: Long): Flow<Response<Unit>> {
        return flow{
            val token = tokenService.getToken()
            val response = postService.unsetLike("Bearer $token", postId)
            if (response.isSuccessful){
                emit(Response.Success(Unit))
            }else{
                emit(Response.Error(response.code(), response.errorBody().toString()))
            }
        }
    }

    override suspend fun getComments(postId: Long): Flow<Response<List<CommentResponse>>> {
        return flow{
            val token = tokenService.getToken()
            val response = postService.getComments("Bearer $token", postId)
            if (response.isSuccessful){
                emit(Response.Success(response.body()!!))
            }else{
                emit(Response.Error(response.code(), response.errorBody().toString()))
            }
        }
    }

    override suspend fun createComment(
        postId: Long,
        comment: CommentRequest
    ): Flow<Response<Unit>> {
        return flow{
            val token = tokenService.getToken()
            val response = postService.createComment("Bearer $token", postId, comment)
            if (response.isSuccessful){
                emit(Response.Success(Unit))
            }else{
                emit(Response.Error(response.code(), response.errorBody().toString()))
            }
        }
    }

    override suspend fun createPost(post: PostRequest): Flow<Response<Unit>> {
        return flow{
            val token = tokenService.getToken()
            val response = postService.createPost("Bearer $token", post)
            if (response.isSuccessful){
                emit(Response.Success(Unit))
            }else{
                emit(Response.Error(response.code(), response.errorBody().toString()))
            }
        }
    }

}