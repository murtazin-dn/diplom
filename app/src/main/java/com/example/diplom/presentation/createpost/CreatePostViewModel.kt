package com.example.diplom.presentation.createpost

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplom.R
import com.example.diplom.data.network.categories.model.response.CategoryResponse
import com.example.diplom.data.network.posts.model.request.PostRequest
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.category.usecase.GetCategoriesUseCase
import com.example.diplom.domain.posts.usecase.CreatePostUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CreatePostViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val createPostUseCase: CreatePostUseCase
) : ViewModel() {

    private val _state = MutableLiveData<CreatePostStateUI>()
    val state: LiveData<CreatePostStateUI> get() = _state

    fun fetchCategories() = viewModelScope.launch {
        getCategoriesUseCase.execute()
            .collect { response ->
                when(response){
                    is Response.Error -> _state.value =
                        CreatePostStateUI.Error(R.string.error_try_again)
                    is Response.Success -> _state.value =
                        CreatePostStateUI.Categories(response.data)
                }
            }
    }

    fun createPost(post: CreatePostFields){
        println("create")
        if(!validateFields(post)) return
        val createPost = PostRequest(
            title = post.title,
            text = post.text,
            categoryId = post.category!!.id
        )
        viewModelScope.launch {
            createPostUseCase.execute(createPost)
                .catch {
                    _state.value = CreatePostStateUI.Error(R.string.error_try_again)
                }
                .collect { response ->
                    when(response){
                        is Response.Error -> _state.value = CreatePostStateUI.Error(R.string.error_try_again)
                        is Response.Success ->  _state.value = CreatePostStateUI.Success(Unit)
                    }
                }
        }
    }

    private fun validateFields(post: CreatePostFields): Boolean {
        var result = true
        errorTitle(post.title)?.let { error ->
            _state.value = CreatePostStateUI.ErrorTitle(error)
            result = false }
        errorText(post.text)?.let { error ->
            _state.value = CreatePostStateUI.ErrorText(error)
            result = false }
        errorCategory(post.category)?.let { error ->
            _state.value = CreatePostStateUI.ErrorCategory(error)
            result = false }
        return result
    }

    private fun errorTitle(title: String): Int? {
        return when{
            title.isEmpty() -> R.string.empty_fields
            title.length >= 200 -> R.string.long_title
            else -> null
        }
    }

    private fun errorText(text: String): Int? {
        return when{
            text.isEmpty() -> R.string.empty_fields
            else -> null
        }
    }

    private fun errorCategory(category: CategoryResponse?): Int? {
        return when (category) {
            null -> R.string.empty_fields
            else -> null
        }
    }
}

sealed class CreatePostStateUI {
    data class Error(val error: Int): CreatePostStateUI()
    data class ErrorTitle(val error: Int?): CreatePostStateUI()
    data class ErrorText(val error: Int?): CreatePostStateUI()
    data class ErrorCategory(val error: Int?): CreatePostStateUI()
    data class Categories(val categories: List<CategoryResponse>): CreatePostStateUI()
    data class Success(val unit: Unit): CreatePostStateUI()
}