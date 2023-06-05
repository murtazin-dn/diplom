package com.example.diplom.presentation.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplom.R
import com.example.diplom.data.network.chats.model.response.ChatResponse
import com.example.diplom.data.network.messages.model.request.MessageRequest
import com.example.diplom.data.network.messages.model.response.MessageResponse
import com.example.diplom.data.network.messages.repository.WebSocketClient
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.chats.usecase.GetChatByChatIdUseCase
import com.example.diplom.domain.chats.usecase.GetChatByUserIdUseCase
import com.example.diplom.domain.messages.usecase.GetMessagesFromMessageIdUseCase
import com.example.diplom.domain.messages.usecase.GetMessagesUseCase
import com.example.diplom.domain.messages.usecase.ReadMessageUseCase
import com.example.diplom.presentation.ui.createpost.CreatePostStateUI
import com.example.diplom.presentation.ui.createpost.PhotosAdapter
import com.example.diplom.presentation.ui.createpost.SelectedPhotoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChatViewModel(
    private val getChatByChatIdUseCase: GetChatByChatIdUseCase,
    private val getChatByUserIdUseCase: GetChatByUserIdUseCase,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val getMessagesFromMessageIdUseCase: GetMessagesFromMessageIdUseCase,
    private val readMessageUseCase: ReadMessageUseCase,
    private val webSocketClient: WebSocketClient
) : ViewModel(), KoinComponent {

    private val _state = MutableLiveData<ChatStateUi>()
    val state: LiveData<ChatStateUi> get() = _state


    private val photosAdapter: PhotosAdapter by inject()

    fun addPhoto(photo: SelectedPhotoModel) = photosAdapter.addPhoto(photo)
    fun reloadPhoto(photo: SelectedPhotoModel) = photosAdapter.reloadPhoto(photo)
    fun getItemsCount(): Int = photosAdapter.getItemsCount()
    fun deletePhoto(id: String) = photosAdapter.deletePhoto(id)
    fun deleteAllPhotos() = photosAdapter.deleteAllPhotos()
    fun isPhotosLoaded() = photosAdapter.isLoadedPhotos()

    init{
        viewModelScope.launch {
            photosAdapter.photos.collect{
                _state.postValue(ChatStateUi.Photos(it))
            }
        }
    }



    fun getChatByChatId(chatId: Long) = viewModelScope.launch(Dispatchers.IO) {
        getChatByChatIdUseCase.execute(chatId)
            .collect{ response ->
                when(response){
                    is Response.Error -> _state.postValue(ChatStateUi.Error(R.string.error_try_again))
                    is Response.Success -> _state.postValue(ChatStateUi.Chat(response.data))
                }
            }
    }
    fun readMessage(chatId: Long, messageId: Long) = viewModelScope.launch(Dispatchers.IO) {
        readMessageUseCase.execute(chatId, messageId)
            .collect{ response ->
                when(response){
                    is Response.Error -> _state.postValue(ChatStateUi.Error(R.string.error_try_again))
                    is Response.Success -> Unit
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
    fun getMessagesFromMessageId(chatId: Long, messageId: Long) = viewModelScope.launch(Dispatchers.IO) {
        getMessagesFromMessageIdUseCase.execute(chatId, messageId)
            .collect{ response ->
                when(response){
                    is Response.Error -> _state.postValue(ChatStateUi.Error(R.string.error_try_again))
                    is Response.Success -> _state.postValue(ChatStateUi.NewMessages(response.data))
                }
            }
    }

    fun setMessageListener(listener: WebSocketClient.SocketListener) {
        webSocketClient.setMessageListener(listener)
    }
    fun setChatId(chatId: Long) {
        webSocketClient.setChatId(chatId)
    }
    fun sendMessage(message: MessageRequest){
        if(!isPhotosLoaded()){
            _state.postValue(ChatStateUi.Error(R.string.photos_not_loaded))
            return
        }
        val list = photosAdapter.getPhotosNameList()
        if (list.isEmpty() && message.text.isEmpty()) {
            ChatStateUi.Error(R.string.empty_fields)
            return
        }
        message.images = list
        val msg = Json.encodeToString(message)
        webSocketClient.sendMessage(msg)
        deleteAllPhotos()
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
    data class NewMessages(val messages: List<MessageResponse>): ChatStateUi()
    data class Photos(val photos: List<SelectedPhotoModel>): ChatStateUi()
}