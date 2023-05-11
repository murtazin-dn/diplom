package com.example.diplom.presentation.auth.signin

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplom.R
import com.example.diplom.data.network.auth.model.request.SignInRequest
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.auth.usecase.SignInUseCase
import com.example.diplom.presentation.common.isEmailValid
import com.example.diplom.util.TokenService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class SignInViewModel(
    private val signInUseCase: SignInUseCase
): ViewModel() {
    private val _state = MutableStateFlow<SignInFragmentState>(SignInFragmentState.Init)
    val state: StateFlow<SignInFragmentState> get() = _state


    fun signIn(signInRequest: SignInRequest) {
        viewModelScope.launch {
            signInUseCase.execute(signInRequest)
                .onStart {
                    setLoading()
                }
                .catch { exception ->
                    hideLoading()
                    _state.value = SignInFragmentState.ErrorSignIn(R.string.error_try_again)
                }
                .collect { result ->
                    hideLoading()
                    when(result){
                        is Response.Error -> _state.value = getError(result)
                        is Response.Success -> {
                            _state.value = SignInFragmentState.SuccessSignIn(true)
                        }
                    }
                }
        }
    }

    fun validateFields(signInRequest: SignInRequest){
        _state.value = SignInFragmentState.ErrorEmail(errorEmailField(signInRequest.email))
        _state.value = SignInFragmentState.ErrorPassword(errorPasswordField(signInRequest.password))
    }

    private fun getError(result: Response.Error) = when(result.code){
        400 -> SignInFragmentState.ErrorSignInStr(result.message)
        401 -> SignInFragmentState.ErrorSignIn(R.string.invalid_credentials)
        else -> SignInFragmentState.ErrorSignIn(R.string.error_try_again)
    }


    private fun errorEmailField(email: String): Int? = when{
        email.isBlank() -> R.string.empty_fields
        !email.isEmailValid() -> R.string.invalid_email
        else -> null
    }

    private fun errorPasswordField(password: String): Int? = when{
        password.isBlank() -> R.string.empty_fields
        password.length < 8  -> R.string.short_password
        password.length > 50  -> R.string.long_password
        else -> null
    }

    private fun setLoading(){
        _state.value = SignInFragmentState.IsLoading(true)
    }

    private fun hideLoading(){
        _state.value = SignInFragmentState.IsLoading(false)
    }

}

sealed class SignInFragmentState {
    object Init : SignInFragmentState()
    data class IsLoading(val isLoading: Boolean) : SignInFragmentState()
    data class SuccessSignIn(val isSignIn: Boolean) : SignInFragmentState()
    data class ErrorSignIn(val errorSignIn: Int) : SignInFragmentState()
    data class ErrorSignInStr(val errorSignIn: String) : SignInFragmentState()
    data class ErrorPassword(val errorFields: Int?) : SignInFragmentState()
    data class ErrorEmail(val errorFields: Int?) : SignInFragmentState()
}