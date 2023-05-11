package com.example.diplom.domain.category.repository

import com.example.diplom.data.network.categories.model.response.CategoryResponse
import com.example.diplom.data.network.util.Response
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun getCategories(): Flow<Response<List<CategoryResponse>>>
    suspend fun getCategoryById(id: Long): Flow<Response<CategoryResponse>>
}