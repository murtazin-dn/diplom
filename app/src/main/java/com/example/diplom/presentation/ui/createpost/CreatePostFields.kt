package com.example.diplom.presentation.ui.createpost

import com.example.diplom.data.network.categories.model.response.CategoryResponse

data class CreatePostFields(
    val title: String,
    val text: String,
    val category: CategoryResponse?
)
