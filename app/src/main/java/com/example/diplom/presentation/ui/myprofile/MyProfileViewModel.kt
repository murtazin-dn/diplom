package com.example.diplom.presentation.ui.myprofile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplom.R
import com.example.diplom.data.network.personinfo.model.response.ProfileResponse
import com.example.diplom.data.network.personinfo.model.response.MyProfileResponse
import com.example.diplom.data.network.posts.model.response.PostResponse
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.personinfo.usecase.GetMyProfileUseCase
import com.example.diplom.domain.personinfo.usecase.GetProfileUseCase
import com.example.diplom.domain.posts.usecase.GetPostsByUserIdUseCase
import com.example.diplom.domain.posts.usecase.SetLikeUseCase
import com.example.diplom.domain.posts.usecase.UnsetLikeUseCase
import com.example.diplom.domain.subscribers.usecase.SubscribeUseCase
import com.example.diplom.domain.subscribers.usecase.UnsubscribeUseCase
import com.example.diplom.presentation.ui.posts.PostsStateUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class MyProfileViewModel(
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val getPostsByUserIdUseCase: GetPostsByUserIdUseCase,
    private val setLikeUseCase: SetLikeUseCase,
    private val unsetLikeUseCase: UnsetLikeUseCase,
    private val subscribeUseCase: SubscribeUseCase,
    private val unsubscribeUseCase: UnsubscribeUseCase
) : ViewModel() {

    private val _state = MutableLiveData<MyPersonInfoStateUI>()
    val state: LiveData<MyPersonInfoStateUI> = _state

    fun getMyProfileInfo() = viewModelScope.launch(){
        getMyProfileUseCase.execute()
            .onStart {
            }
            .catch {
                _state.value = MyPersonInfoStateUI.Error(R.string.error_try_again)
            }
            .collect{ result ->
                when(result){
                    is Response.Error -> _state.value = MyPersonInfoStateUI.Error(R.string.error_try_again)
                    is Response.Success -> _state.value = MyPersonInfoStateUI.MyProfileInfo(result.data)
                }
            }
    }

    fun getProfileInfo(userId: Long) = viewModelScope.launch(){
        getProfileUseCase.execute(userId)
            .onStart {
            }
            .catch {
                _state.value = MyPersonInfoStateUI.Error(R.string.error_try_again)
            }
            .collect{ result ->
                when(result){
                    is Response.Error -> _state.value = MyPersonInfoStateUI.Error(R.string.error_try_again)
                    is Response.Success -> _state.value = MyPersonInfoStateUI.ProfileInfo(result.data)
                }
            }
    }

    fun getPosts(userId: Long) = viewModelScope.launch{
        getPostsByUserIdUseCase.execute(userId)
            .catch {
                _state.value = MyPersonInfoStateUI.Error(R.string.error_try_again)
            }
            .collect{ result ->
                when(result){
                    is Response.Error -> _state.value = MyPersonInfoStateUI.Error(R.string.error_try_again)
                    is Response.Success -> _state.value = MyPersonInfoStateUI.Posts(result.data)
                }
            }
    }

    fun setLike(postId: Long) = viewModelScope.launch{
        setLikeUseCase.execute(postId)
            .catch {
                _state.value = MyPersonInfoStateUI.Error(R.string.error_try_again)
            }
            .collect{ result ->
                when(result){
                    is Response.Error -> _state.value = MyPersonInfoStateUI.Error(R.string.error_try_again)
                    is Response.Success -> _state.value = MyPersonInfoStateUI.SetLike(postId)
                }
            }
    }

    fun unsetLike(postId: Long) = viewModelScope.launch{
        unsetLikeUseCase.execute(postId)
            .catch {
                _state.value = MyPersonInfoStateUI.Error(R.string.error_try_again)
            }
            .collect{ result ->
                when(result){
                    is Response.Error -> _state.value = MyPersonInfoStateUI.Error(R.string.error_try_again)
                    is Response.Success -> _state.value = MyPersonInfoStateUI.UnsetLike(postId)
                }
            }
    }
    fun subscribe(userId: Long) = viewModelScope.launch(Dispatchers.IO){
        subscribeUseCase.execute(userId)
            .catch {
                _state.postValue(MyPersonInfoStateUI.Error(R.string.error_try_again))
            }
            .collect{ response ->
                when(response){
                    is Response.Error -> _state.postValue(MyPersonInfoStateUI.Error(R.string.error_try_again))
                    is Response.Success -> _state.postValue(MyPersonInfoStateUI.Subscribe(response.data))
                }

            }
    }
    fun unsubscribe(userId: Long) = viewModelScope.launch(Dispatchers.IO){
        unsubscribeUseCase.execute(userId)
            .catch {
                _state.postValue(MyPersonInfoStateUI.Error(R.string.error_try_again))
            }
            .collect{ response ->
                when(response){
                    is Response.Error -> _state.postValue(MyPersonInfoStateUI.Error(R.string.error_try_again))
                    is Response.Success -> _state.postValue(MyPersonInfoStateUI.Subscribe(response.data))
                }

            }
    }
}

sealed class MyPersonInfoStateUI{
    data class MyProfileInfo(val profileInfo: MyProfileResponse): MyPersonInfoStateUI()
    data class ProfileInfo(val profileInfo: ProfileResponse): MyPersonInfoStateUI()
    data class SetLike(val postId: Long): MyPersonInfoStateUI()
    data class UnsetLike(val postId: Long): MyPersonInfoStateUI()
    data class Posts(val postResponse: List<PostResponse>): MyPersonInfoStateUI()
    data class Error(val error: Int): MyPersonInfoStateUI()
    data class Subscribe(val subscribe: Unit): MyPersonInfoStateUI()
    data class Unsubscribe(val unsubscribe: Unit): MyPersonInfoStateUI()
}