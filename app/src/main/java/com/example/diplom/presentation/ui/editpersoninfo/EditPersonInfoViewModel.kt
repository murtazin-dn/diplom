package com.example.diplom.presentation.ui.editpersoninfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplom.R
import com.example.diplom.data.network.categories.model.response.CategoryResponse
import com.example.diplom.data.network.personinfo.model.response.MyProfileResponse
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.category.usecase.GetCategoriesUseCase
import com.example.diplom.domain.personinfo.usecase.GetMyProfileUseCase
import com.example.diplom.domain.personinfo.usecase.UploadUserPhotoUseCase
import kotlinx.coroutines.launch
import java.io.File

class EditPersonInfoViewModel(
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val uploadUserPhotoUseCase: UploadUserPhotoUseCase
) : ViewModel() {

    private val _state = MutableLiveData<EditPersonInfoStateUi>()
    val state: LiveData<EditPersonInfoStateUi> get() = _state

    fun getPersonInfo() = viewModelScope.launch {
        getMyProfileUseCase.execute()
            .collect{ response ->
                when(response){
                    is Response.Error -> _state.value = EditPersonInfoStateUi.Error(R.string.error_try_again)
                    is Response.Success -> _state.value = EditPersonInfoStateUi.PersonInfo(response.data)
                }
            }
    }

    fun fetchCategories() = viewModelScope.launch {
        getCategoriesUseCase.execute()
            .collect { response ->
                when(response){
                    is Response.Error -> _state.value =
                        EditPersonInfoStateUi.Error(R.string.error_try_again)
                    is Response.Success -> _state.value =
                        EditPersonInfoStateUi.Categories(response.data)
                }
            }
    }

    fun uploadUserPhoto(file: File) = viewModelScope.launch{
        uploadUserPhotoUseCase.execute(file)
//            .catch { error ->
//                when(error){
//
//                }
//                println("file is absent")
//                _state.value = EditPersonInfoStateUi.Error(R.string.error_try_again)
//            }
            .collect { response ->
                when(response){
                    is Response.Error -> _state.value = EditPersonInfoStateUi.Error(R.string.error_try_again)
                    is Response.Success -> _state.value = EditPersonInfoStateUi.UserPhoto(response.data)
                }
            }
    }
}

sealed class EditPersonInfoStateUi{
    data class Error(val error: Int): EditPersonInfoStateUi()
    data class PersonInfo(val personInfo: MyProfileResponse): EditPersonInfoStateUi()
    data class Categories(val categories: List<CategoryResponse>): EditPersonInfoStateUi()
    data class UserPhoto(val photo: String): EditPersonInfoStateUi()
}