package com.example.diplom.di

import com.example.diplom.data.network.categories.repository.CategoryRepositoryImpl
import com.example.diplom.domain.category.repository.CategoryRepository
import com.example.diplom.domain.category.usecase.GetCategoriesUseCase
import com.example.diplom.domain.category.usecase.GetCategoryByIdUseCase
import org.koin.dsl.module

val categoryModule = module {
    single<CategoryRepository> { CategoryRepositoryImpl( categoryService = get()) }
    single { GetCategoriesUseCase(categoryRepository = get()) }
    single { GetCategoryByIdUseCase(categoryRepository = get()) }
}