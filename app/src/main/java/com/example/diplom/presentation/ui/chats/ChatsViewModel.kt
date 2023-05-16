package com.example.diplom.presentation.ui.chats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplom.R
import com.example.diplom.data.network.chats.model.response.ChatPreview
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.chats.usecase.GetChatsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatsViewModel(
    private val getChatsUseCase: GetChatsUseCase
) : ViewModel() {

    private val _state = MutableLiveData<ChatsStateUi>()
    val state: LiveData<ChatsStateUi> get() = _state

    fun getChats() = viewModelScope.launch(Dispatchers.IO) {
        getChatsUseCase.execute()
            .collect{ result ->
                when(result){
                    is Response.Error -> _state.postValue(ChatsStateUi.Error(R.string.error_try_again))
                    is Response.Success -> _state.postValue(ChatsStateUi.Chats(result.data))
                }
            }
    }
}

sealed class ChatsStateUi{
    data class Error(val error: Int): ChatsStateUi()
    data class Chats(val chats: List<ChatPreview>): ChatsStateUi()
}