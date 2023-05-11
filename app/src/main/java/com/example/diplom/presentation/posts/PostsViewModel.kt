package com.example.diplom.presentation.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplom.R
import com.example.diplom.data.network.posts.model.response.PostResponse
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.auth.usecase.IsMyUserIdUseCase
import com.example.diplom.domain.posts.usecase.*
import com.example.diplom.domain.subscribers.usecase.SubscribeUseCase
import com.example.diplom.domain.subscribers.usecase.UnsubscribeUseCase
import com.example.diplom.presentation.myprofile.MyPersonInfoStateUI
import com.example.diplom.presentation.post.PostStateUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PostsViewModel(
    private val getMySubscriptionsPostUseCase: GetMySubscriptionsPostUseCase,
    private val getAllPostsUseCase: GetAllPostsUseCase,
    private val getPostsByUserIdUseCase: GetPostsByUserIdUseCase,
    private val setLikeUseCase: SetLikeUseCase,
    private val unsetLikeUseCase: UnsetLikeUseCase,
) : ViewModel() {

    private val _state = MutableLiveData<PostsStateUI>()
    val state: LiveData<PostsStateUI> get() = _state


    fun setLike(postId: Long) = viewModelScope.launch{
        setLikeUseCase.execute(postId)
            .catch {
                _state.value = PostsStateUI.Error(R.string.error_try_again)
            }
            .collect{ result ->
                when(result){
                    is Response.Error -> _state.value = PostsStateUI.Error(R.string.error_try_again)
                    is Response.Success -> _state.value = PostsStateUI.SetLike(Unit)
                }
            }
    }

    fun unsetLike(postId: Long) = viewModelScope.launch{
        unsetLikeUseCase.execute(postId)
            .catch {
                _state.value = PostsStateUI.Error(R.string.error_try_again)
            }
            .collect{ result ->
                when(result){
                    is Response.Error -> _state.value = PostsStateUI.Error(R.string.error_try_again)
                    is Response.Success -> _state.value = PostsStateUI.UnsetLike(Unit)
                }
            }
    }

    fun getAllPosts() = viewModelScope.launch(Dispatchers.IO){
        getAllPostsUseCase.execute()
            .catch {
                _state.postValue(PostsStateUI.Error(R.string.error_try_again))
            }
            .collect{ response ->
                when(response){
                    is Response.Error -> _state.postValue(PostsStateUI.Error(R.string.error_try_again))
                    is Response.Success -> _state.postValue(PostsStateUI.Posts(response.data))
                }

            }
    }
    fun getMySubscriptionsPosts() = viewModelScope.launch(Dispatchers.IO){
        getMySubscriptionsPostUseCase.execute()
            .catch {
                _state.postValue(PostsStateUI.Error(R.string.error_try_again))
            }
            .collect{ response ->
                when(response){
                    is Response.Error -> _state.postValue(PostsStateUI.Error(R.string.error_try_again))
                    is Response.Success -> _state.postValue(PostsStateUI.Posts(response.data))
                }

            }
    }
    fun getPostsByUserId(userId: Long) = viewModelScope.launch(Dispatchers.IO){
        getPostsByUserIdUseCase.execute(userId)
            .catch {
                _state.postValue(PostsStateUI.Error(R.string.error_try_again))
            }
            .collect{ response ->
                when(response){
                    is Response.Error -> _state.postValue(PostsStateUI.Error(R.string.error_try_again))
                    is Response.Success -> _state.postValue(PostsStateUI.Posts(response.data))
                }

            }
    }
}

sealed class PostsStateUI{
    data class Error(val error: Int): PostsStateUI()
    data class Posts(val posts: List<PostResponse>): PostsStateUI()
    data class SetLike(val unit: Unit): PostsStateUI()
    data class UnsetLike(val unit: Unit): PostsStateUI()

}