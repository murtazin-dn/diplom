package com.example.diplom.presentation.subscribers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplom.R
import com.example.diplom.data.network.personinfo.model.response.ProfileResponse
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.auth.usecase.IsMyUserIdUseCase
import com.example.diplom.domain.personinfo.usecase.FindUsersUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SearchViewModel(
    private val findUsersUseCase: FindUsersUseCase,
    private val isMyUserIdUseCase: IsMyUserIdUseCase
) : ViewModel() {
    private val _state = MutableLiveData<SearchStateUI>()
    val state: LiveData<SearchStateUI> get() = _state

    fun getUsers(text: String) = viewModelScope.launch {
        findUsersUseCase.execute(text)
            .catch {

            }
            .collect{ response ->
                when(response){
                    is Response.Error -> _state.postValue(SearchStateUI.Error(R.string.error_try_again))
                    is Response.Success -> _state.value = SearchStateUI.Users(response.data)
                }
            }
    }

    fun isMyUserId(id: Long) = isMyUserIdUseCase.isMyUserId(id)
}

sealed class SearchStateUI{
    data class Error(val error: Int): SearchStateUI()
    data class Users(val users: List<ProfileResponse>): SearchStateUI()
}