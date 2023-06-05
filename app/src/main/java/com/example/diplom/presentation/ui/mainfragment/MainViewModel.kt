package com.example.diplom.presentation.ui.mainfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplom.R
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.chats.usecase.GetUnreadDialogsCountUseCase
import com.example.diplom.presentation.ui.chat.ChatStateUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val getUnreadDialogsCountUseCase: GetUnreadDialogsCountUseCase
) : ViewModel() {
    private val _state = MutableLiveData<MainFragmentStateUi>()
    val state: LiveData<MainFragmentStateUi> get() = _state

    fun getUnreadDialogsCount() = viewModelScope.launch(Dispatchers.IO) {
        getUnreadDialogsCountUseCase.execute()
            .collect{ response ->
                when(response){
                    is Response.Error -> _state.postValue(MainFragmentStateUi.Error(R.string.error_try_again))
                    is Response.Success -> _state.postValue(MainFragmentStateUi.UnreadMessagesCount(response.data))
                }
            }
    }
}

sealed class MainFragmentStateUi{
    data class Error(val error: Int): MainFragmentStateUi()
    data class UnreadMessagesCount(val count: Long): MainFragmentStateUi()
}