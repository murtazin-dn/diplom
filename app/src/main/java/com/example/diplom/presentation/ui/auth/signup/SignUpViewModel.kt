package com.example.diplom.presentation.ui.auth.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplom.R
import com.example.diplom.data.network.auth.model.request.SignUpRequest
import com.example.diplom.data.network.categories.model.response.CategoryResponse
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.auth.usecase.IsTakenEmailUseCase
import com.example.diplom.domain.auth.usecase.SignUpUseCase
import com.example.diplom.domain.category.usecase.GetCategoriesUseCase
import com.example.diplom.presentation.common.isEmailValid
import com.example.diplom.presentation.common.isNameValid
import com.example.diplom.presentation.common.toEpochSeconds
import com.example.diplom.presentation.ui.auth.signin.SignInFragmentState
import com.example.diplom.presentation.ui.auth.signup.model.SignUpFields
import com.example.diplom.util.TokenService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val signUpUseCase: SignUpUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val isTakenEmailUseCase: IsTakenEmailUseCase
): ViewModel() {
    private val _state = MutableLiveData<SignUpFragmentState>()
    val state: LiveData<SignUpFragmentState> get() = _state


    fun signUp(signUpRequest: SignUpRequest) = viewModelScope.launch {
        signUpUseCase.execute(signUpRequest)
            .onStart {
            }
            .catch { exception ->
                hideLoading()
                Log.e("signup", "error ${exception.message}")
                _state.value = SignUpFragmentState.ErrorSignUp(R.string.error_try_again)
            }
            .collect { result ->
                hideLoading()
                when(result){
                    is Response.Error -> _state.value =
                        SignUpFragmentState.ErrorSignUpStr(result.message)
                    is Response.Success -> {
                        _state.value = SignUpFragmentState.SuccessSignUp(true)
                    }
                }
            }
    }

    fun fetchCategories() = viewModelScope.launch {
        getCategoriesUseCase.execute()
            .catch { exception ->
                Log.e("categories", "error ${exception.message}")
                _state.value = SignUpFragmentState.ErrorSignUp(R.string.error_try_again)
            }
            .collect { result ->
                hideLoading()
                when(result){
                    is Response.Error -> _state.value =
                        SignUpFragmentState.ErrorSignUpStr(result.message)
                    is Response.Success -> _state.value =
                        SignUpFragmentState.SuccessFetchCategories(result.data)
                }
            }
    }
    fun isTakenEmail(email: String) = viewModelScope.launch {
        isTakenEmailUseCase.execute(email)
            .onStart {
                setLoading()
            }
            .catch { exception ->
                _state.value = SignUpFragmentState.ErrorSignUp(R.string.error_try_again)
                Log.e("email", "error ${exception.message}")
            }
            .collect { result ->
                hideLoading()
                when(result){
                    is Response.Error -> _state.value =
                        SignUpFragmentState.ErrorSignUpStr(result.message)
                    is Response.Success -> _state.value =
                        SignUpFragmentState.SuccessIsTakenEmail(result.data)
                }
            }
    }




    private fun setLoading(){
        _state.value = SignUpFragmentState.IsLoading(true)
    }

    private fun hideLoading(){
        _state.value = SignUpFragmentState.IsLoading(false)
    }

    fun validateFields(signUpFields: SignUpFields): Boolean {
        var result = true
        errorEmailField(signUpFields.email).let { error ->
            _state.value = SignUpFragmentState.ErrorEmail(error)
            if (error != null) result = false
        }
        errorPasswordField(signUpFields.password).let { error ->
            _state.value = SignUpFragmentState.ErrorPassword(error)
            if (error != null) result = false
        }
        errorConfirmPasswordField(signUpFields.password, signUpFields.confirmPassword).let { error ->
            _state.value = SignUpFragmentState.ErrorConfirmPassword(error)
            if (error != null) result = false
        }
        errorNameField(signUpFields.name).let { error ->
            _state.value = SignUpFragmentState.ErrorName(error)
            if (error != null) result = false
        }
        errorSurnameField(signUpFields.surname).let { error ->
            _state.value = SignUpFragmentState.ErrorSurname(error)
            if (error != null) result = false
        }
        errorDateOfBirthdayField(signUpFields.dateOfBirthday).let { error ->
            _state.value = SignUpFragmentState.ErrorDateOfBirthday(error)
            if (error != null) result = false
        }
        errorCategoryField(signUpFields.categoryId).let { error ->
            _state.value = SignUpFragmentState.ErrorCategory(error)
            if (error != null) result = false
        }
        return result
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

    private fun errorConfirmPasswordField(password: String, confirmPassword: String): Int? = when{
        confirmPassword.isBlank() -> R.string.empty_fields
        password != confirmPassword -> R.string.error_confirm_password
        else -> null
    }

    private fun errorNameField(name: String): Int? = when{
        name.isEmpty() -> R.string.empty_fields
        !name[0].isUpperCase() -> R.string.error_upper_case_name
        name.length > 50 -> R.string.long_name
        !name.isNameValid() -> R.string.invalid_name
        else -> null
    }

    private fun errorSurnameField(surname: String): Int? = when{
        surname.isEmpty() -> R.string.empty_fields
        !surname[0].isUpperCase() -> R.string.error_upper_case_surname
        surname.length > 50 -> R.string.long_surname
        !surname.isNameValid() -> R.string.invalid_surname
        else -> null
    }

    private fun errorDateOfBirthdayField(dateOfBirthday: String): Int?{
        if (dateOfBirthday.isEmpty()) return R.string.empty_fields
        val date = try {
            dateOfBirthday.toEpochSeconds()
        } catch (e: Exception) {
            return R.string.invalid_date_of_birthday
        }
        return when{
            date > System.currentTimeMillis()/1000 -> R.string.invalid_date_of_birthday
            date < -2180836953 -> R.string.invalid_date_of_birthday
            else -> null
        }
    }
    private fun errorCategoryField(categoryId: Long?): Int?{
        return if (categoryId == null) R.string.empty_fields else null
    }


}

sealed class SignUpFragmentState {
    object Init : SignUpFragmentState()
    data class IsLoading(val isLoading: Boolean) : SignUpFragmentState()
    data class SuccessSignUp(val isSignUp: Boolean) : SignUpFragmentState()
    data class SuccessFetchCategories(val categories: List<CategoryResponse>) : SignUpFragmentState()
    data class SuccessIsTakenEmail(val isTakenEmail: Boolean) : SignUpFragmentState()
    data class ErrorSignUpStr(val errorSignUp: String) : SignUpFragmentState()
    data class ErrorSignUp(val errorSignUp: Int) : SignUpFragmentState()
    data class ErrorEmail(val errorEmail: Int?) : SignUpFragmentState()
    data class ErrorPassword(val errorPassword: Int?) : SignUpFragmentState()
    data class ErrorConfirmPassword(val errorConfirmPassword: Int?) : SignUpFragmentState()
    data class ErrorName(val errorName: Int?) : SignUpFragmentState()
    data class ErrorSurname(val errorSurname: Int?) : SignUpFragmentState()
    data class ErrorDateOfBirthday(val errorDateOfBirthday: Int?) : SignUpFragmentState()
    data class ErrorCategory(val errorCategory: Int?) : SignUpFragmentState()
}