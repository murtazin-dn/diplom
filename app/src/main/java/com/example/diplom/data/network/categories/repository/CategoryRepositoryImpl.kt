package com.example.diplom.data.network.categories.repository

import com.example.diplom.data.network.categories.model.response.CategoriesResponse
import com.example.diplom.data.network.categories.model.response.CategoryResponse
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.category.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CategoryRepositoryImpl(
    private val categoryService: CategoryService
): CategoryRepository {
    override suspend fun getCategories(): Flow<Response<CategoriesResponse>> {
        return flow {
            val response = categoryService.getCategories()
            if (response.isSuccessful){
                val body = response.body()!!
                emit(Response.Success(body))
            }else{
                emit(Response.Error(response.code(), response.errorBody().toString()))
            }
        }
    }

    override suspend fun getCategoryById(id: Long): Flow<Response<CategoryResponse>> {
        return flow {
            val response = categoryService.getCategoryById(id)
            if (response.isSuccessful){
                val body = response.body()!!
                emit(Response.Success(body))
            }else{
                emit(Response.Error(response.code(), response.errorBody().toString()))
            }
        }
    }
}