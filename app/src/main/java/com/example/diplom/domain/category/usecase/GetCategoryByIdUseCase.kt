package com.example.diplom.domain.category.usecase

import com.example.diplom.data.network.categories.model.response.CategoryResponse
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.category.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow

class GetCategoryByIdUseCase(
    private val categoryRepository: CategoryRepository
) {
    suspend fun execute(id: Long): Flow<Response<CategoryResponse>> =
        categoryRepository.getCategoryById(id)
}