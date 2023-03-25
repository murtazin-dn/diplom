package com.example.diplom.data.network.categories.repository

import com.example.diplom.data.network.categories.model.response.CategoriesResponse
import com.example.diplom.data.network.categories.model.response.CategoryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CategoryService {

    @GET("category")
    suspend fun getCategories(): Response<CategoriesResponse>

    @GET("category/{categoryId}")
    suspend fun getCategoryById(@Path("categoryId") id: Long): Response<CategoryResponse>
}