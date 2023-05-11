package com.example.diplom.presentation.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplom.R
import com.example.diplom.data.network.chats.model.response.ChatResponse
import com.example.diplom.data.network.messages.model.response.MessageResponse
import com.example.diplom.data.network.messages.repository.WebSocketClient
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.chats.usecase.GetChatByChatIdUseCase
import com.example.diplom.domain.chats.usecase.GetChatByUserIdUseCase
import com.example.diplom.domain.messages.usecase.GetMessagesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel(
    private val getChatByChatIdUseCase: GetChatByChatIdUseCase,
    private val getChatByUserIdUseCase: GetChatByUserIdUseCase,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val webSocketClient: WebSocketClient
) : ViewModel() {

    private val _state = MutableLiveData<ChatStateUi>()
    val state: LiveData<ChatStateUi> get() = _state

    fun getChatByChatId(chatId: Long) = viewModelScope.launch(Dispatchers.IO) {
        getChatByChatIdUseCase.execute(chatId)
            .collect{ response ->
                when(response){
                    is Response.Error -> _state.postValue(ChatStateUi.Error(R.string.error_try_again))
                    is Response.Success -> _state.postValue(ChatStateUi.Chat(response.data))
                }
            }
    }
    fun getChatByUserId(chatId: Long) = viewModelScope.launch(Dispatchers.IO) {
        getChatByUserIdUseCase.execute(chatId)
            .collect{ response ->
                when(response){
                    is Response.Error -> _state.postValue(ChatStateUi.Error(R.string.error_try_again))
                    is Response.Success -> _state.postValue(ChatStateUi.Chat(response.data))
                }
            }
    }
    fun getMessages(chatId: Long) = viewModelScope.launch(Dispatchers.IO) {
        getMessagesUseCase.execute(chatId)
            .collect{ response ->
                when(response){
                    is Response.Error -> _state.postValue(ChatStateUi.Error(R.string.error_try_again))
                    is Response.Success -> _state.postValue(ChatStateUi.Messages(response.data))
                }
            }
    }

    fun setMessageListener(listener: WebSocketClient.SocketListener) {
        webSocketClient.setMessageListener(listener)
    }
    fun setChatId(chatId: Long) {
        webSocketClient.setChatId(chatId)
    }
    fun sendMessage(message: String){
        webSocketClient.sendMessage(message)
    }
    fun connectWebSocket(){
        webSocketClient.connect()
    }
    fun disconnectWebSocket(){
        webSocketClient.disconnect()
    }
}

sealed class ChatStateUi{
    data class Error(val error: Int): ChatStateUi()
    data class Chat(val chat: ChatResponse): ChatStateUi()
    data class Messages(val messages: List<MessageResponse>): ChatStateUi()
}