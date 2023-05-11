package com.example.diplom.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplom.R
import com.example.diplom.data.network.posts.model.response.PostResponse
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.auth.usecase.IsMyUserIdUseCase
import com.example.diplom.domain.posts.usecase.GetMySubscriptionsPostUseCase
import kotlinx.coroutines.launch

class NewsViewModel(
    private val isMyUserIdUseCase: IsMyUserIdUseCase
) : ViewModel() {

    private val _state = MutableLiveData<NewsStateUI>()
    val state: LiveData<NewsStateUI> get() = _state

    fun isMyUserId(id: Long) = isMyUserIdUseCase.isMyUserId(id)

}

sealed class NewsStateUI{
    data class Error(val error: Int): NewsStateUI()
    data class News(val news: List<PostResponse>): NewsStateUI()
}