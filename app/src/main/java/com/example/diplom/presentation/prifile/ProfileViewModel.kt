package com.example.diplom.presentation.prifile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplom.R
import com.example.diplom.data.network.personinfo.model.response.MyProfileResponse
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.personinfo.usecase.GetMyProfileUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getMyProfileUseCase: GetMyProfileUseCase
) : ViewModel() {

    private val _state = MutableLiveData<ProfileStateUI>()
    val state: LiveData<ProfileStateUI> get() = _state

    fun getMyPersonInfo() = viewModelScope.launch(){
        getMyProfileUseCase.execute()
            .onStart {
            }
            .catch {
                _state.value = ProfileStateUI.Error(R.string.error_try_again)
            }
            .collect{ result ->
                when(result){
                    is Response.Error -> _state.value = ProfileStateUI.Error(R.string.error_try_again)
                    is Response.Success -> _state.value = ProfileStateUI.PersonInfo(result.data)
                }
            }
    }
}

sealed class ProfileStateUI{
    data class Error(val error: Int): ProfileStateUI()
    data class PersonInfo(val personInfo: MyProfileResponse): ProfileStateUI()
}