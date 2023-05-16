package com.example.diplom.presentation.ui.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplom.data.network.posts.model.request.CommentRequest
import com.example.diplom.data.network.posts.model.response.CommentResponse
import com.example.diplom.data.network.posts.model.response.PostResponse
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.posts.usecase.CreateCommentUseCase
import com.example.diplom.domain.posts.usecase.GetCommentsUseCase
import com.example.diplom.domain.posts.usecase.GetPostByIdUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostViewModel(
    private val getCommentsUseCase: GetCommentsUseCase,
    private val getPostByIdUseCase: GetPostByIdUseCase,
    private val createCommentUseCase: CreateCommentUseCase
) : ViewModel() {

    private val _state = MutableLiveData<PostStateUi>()
    val state: LiveData<PostStateUi> get() = _state

    fun getComments(postId: Long) = viewModelScope.launch(Dispatchers.IO){
        getCommentsUseCase.execute(postId)
            .collect { response ->
                when (response){
                    is Response.Error -> TODO()
                    is Response.Success -> _state.postValue(PostStateUi.Comments(response.data))
                }
            }
    }
    fun getPost(postId: Long) = viewModelScope.launch(Dispatchers.IO){
        getPostByIdUseCase.execute(postId)
            .collect { response ->
                when (response){
                    is Response.Error -> TODO()
                    is Response.Success -> _state.postValue(PostStateUi.Post(response.data))
                }
            }
    }
    fun createComment(postId: Long, text: String) = viewModelScope.launch(Dispatchers.IO){
        createCommentUseCase.execute(postId, CommentRequest(text))
            .collect { response ->
                when (response){
                    is Response.Error -> TODO()
                    is Response.Success -> _state.postValue(PostStateUi.Comment(Unit))
                }
            }
    }
}

sealed class PostStateUi {
    data class Error(val error: Int): PostStateUi()
    data class Post(val post: PostResponse): PostStateUi()
    data class Comments(val comments: List<CommentResponse>): PostStateUi()
    data class Comment(val unit: Unit): PostStateUi()
}
