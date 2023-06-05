package com.example.diplom.presentation.ui.settings

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.notifications.usecase.UnsubscribeNotificationsUseCase
import com.example.diplom.util.TokenService
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val tokenService: TokenService,
    private val unsubscribeNotificationsUseCase: UnsubscribeNotificationsUseCase
) : ViewModel() {

    private val _state = MutableLiveData<SettingsStateUi>()
    val state: LiveData<SettingsStateUi> get() = _state

    fun logout() = viewModelScope.launch{
        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                val token = task.result
                unsubscribe(token)
            })
    }

    fun unsubscribe(token: String) = viewModelScope.launch {
        unsubscribeNotificationsUseCase.execute(token)
            .collect{ response ->
                when(response){
                    is Response.Error -> clearToken()
                    is Response.Success -> clearToken()
                }
            }
    }

    fun clearToken(){
        tokenService.clearToken()
        _state.value = SettingsStateUi.Logout(Unit)
    }

}
sealed class SettingsStateUi{
    data class Logout(val logout: Unit): SettingsStateUi()
}