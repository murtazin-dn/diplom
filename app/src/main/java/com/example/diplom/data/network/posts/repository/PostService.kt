package com.example.diplom.data.network.posts.repository

import com.example.diplom.data.network.posts.model.request.CommentRequest
import com.example.diplom.data.network.posts.model.request.PostRequest
import com.example.diplom.data.network.posts.model.response.CommentResponse
import com.example.diplom.data.network.posts.model.response.PostResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PostService {

    @GET("posts")
    suspend fun getMySubscriptionsPosts(
        @Header("Authorization") token: String):
            Response<List<PostResponse>>

    @GET("posts")
    suspend fun getPostsByUserId(
        @Header("Authorization") token: String, @Query("userId") userId: Long):
            Response<List<PostResponse>>

    @GET("posts")
    suspend fun getPostsByCategoryId(
        @Header("Authorization") token: String, @Query("categoryId") categoryId: Long):
            Response<List<PostResponse>>

    @GET("posts")
    suspend fun getAllPosts(
        @Header("Authorization") token: String, @Query("all") vse: Boolean):
            Response<List<PostResponse>>

    @GET("posts/{postId}")
    suspend fun getPostById(
        @Header("Authorization") token: String, @Path("postId") postId: Long):
            Response<PostResponse>

    @POST("posts/{postId}/likes")
    suspend fun setLike(@Header("Authorization") token: String, @Path("postId") postId: Long):
            Response<Unit>

    @DELETE("posts/{postId}/likes")
    suspend fun unsetLike(@Header("Authorization") token: String, @Path("postId") postId: Long):
            Response<Unit>

    @GET("posts/{postId}/comments")
    suspend fun getComments(@Header("Authorization") token: String, @Path("postId") postId: Long):
            Response<List<CommentResponse>>

    @POST("posts/{postId}/comments")
    suspend fun createComment(
        @Header("Authorization") token: String,
        @Path("postId") postId: Long,
        @Body comment: CommentRequest
    ): Response<Unit>

    @POST("posts")
    suspend fun createPost(
        @Header("Authorization") token: String,
        @Body post: PostRequest
    ): Response<Unit>
}