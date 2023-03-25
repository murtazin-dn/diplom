package com.example.diplom.presentation.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplom.R
import com.example.diplom.data.network.auth.model.request.SignUpRequest
import com.example.diplom.data.network.categories.model.response.CategoriesResponse
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.auth.usecase.SignUpUseCase
import com.example.diplom.domain.category.usecase.GetCategoriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val signUpUseCase: SignUpUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
): ViewModel() {
    private val _state = MutableStateFlow<SignUpFragmentState>(SignUpFragmentState.Init)
    val state: StateFlow<SignUpFragmentState> get() = _state


    fun signUp(signUpRequest: SignUpRequest) = viewModelScope.launch {
        signUpUseCase.execute(signUpRequest)
            .onStart {
                setLoading()
            }
            .catch { exception ->
                hideLoading()
//                    showToast(exception.message.toString())
            }
            .collect { result ->
                hideLoading()
                when(result){
                    is Response.Error -> _state.value = SignUpFragmentState.ErrorSignUp(result.message)
                    is Response.Success -> _state.value = SignUpFragmentState.SuccessSignUp(true)
                }
            }
    }

    fun fetchCategories() = viewModelScope.launch {
        getCategoriesUseCase.execute()
            .catch { exception ->
                    Log.e("categories", "error ${exception.message}")
            }
            .collect { result ->
                hideLoading()
                when(result){
                    is Response.Error -> _state.value = SignUpFragmentState.ErrorSignUp(result.message)
                    is Response.Success -> _state.value = SignUpFragmentState.SuccessFetchCategories(result.data)
                }
            }
    }




    private fun setLoading(){
        _state.value = SignUpFragmentState.IsLoading(true)
    }

    private fun hideLoading(){
        _state.value = SignUpFragmentState.IsLoading(false)
    }

}

sealed class SignUpFragmentState {
    object Init : SignUpFragmentState()
    data class IsLoading(val isLoading: Boolean) : SignUpFragmentState()
    data class SuccessSignUp(val isSignUp: Boolean) : SignUpFragmentState()
    data class ErrorSignUp(val errorSignUp: String) : SignUpFragmentState()
    data class SuccessFetchCategories(val categories: CategoriesResponse) : SignUpFragmentState()
    data class ErrorLogin(val errorLogin: Int?) : SignUpFragmentState()
    data class ErrorPassword(val errorPassword: Int?) : SignUpFragmentState()
}