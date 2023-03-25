package com.example.diplom.domain.category.usecase

import com.example.diplom.data.network.categories.model.response.CategoriesResponse
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.category.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow

class GetCategoriesUseCase(
    private val categoryRepository: CategoryRepository
) {
    suspend fun execute(): Flow<Response<CategoriesResponse>> =
        categoryRepository.getCategories()
}