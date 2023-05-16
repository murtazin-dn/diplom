package com.example.diplom.presentation.ui.createpost

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplom.R
import com.example.diplom.data.network.categories.model.response.CategoryResponse
import com.example.diplom.data.network.posts.model.request.PostRequest
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.category.usecase.GetCategoriesUseCase
import com.example.diplom.domain.personinfo.usecase.UploadPhotoUseCase
import com.example.diplom.domain.posts.usecase.CreatePostUseCase
import com.example.diplom.presentation.ui.editpersoninfo.EditPersonInfoStateUi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

class CreatePostViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val createPostUseCase: CreatePostUseCase,
    private val uploadPhotoUseCase: UploadPhotoUseCase
) : ViewModel(), KoinComponent {

    private val _state = MutableLiveData<CreatePostStateUI>()
    val state: LiveData<CreatePostStateUI> get() = _state

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
                _state.postValue(CreatePostStateUI.Photos(it))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        photosAdapter.cleanUp()
    }
//
//    private var photos = mutableListOf<SelectedPhotoModel>()
//
//    fun addPhotos(list: List<SelectedPhotoModel>){
//        for(photo in list){
//            photos.add(photo)
//            uploadPhoto(photo)
//        }
//    }
//
//    fun getItemsCount(): Int = photos.size

//    fun deletePhoto(id: String){
//        photos.find { it.id == id }?.let {
//            it.file.delete()
//            photos.remove(it)
//        }
//        submitList()
//    }
//
//    private fun setStatePhoto(id: String, state: PhotoLoadedStatus){
//        photos = photos.map {
//            if(it.id == id) it.status = state
//            it
//        }.toMutableList()
//        submitList()
//    }
//
//    private fun submitList() = _state.postValue(CreatePostStateUI.Photos(photos.toList()))

    fun fetchCategories() = viewModelScope.launch {
        getCategoriesUseCase.execute()
            .collect { response ->
                when(response){
                    is Response.Error -> _state.value =
                        CreatePostStateUI.Error(R.string.error_try_again)
                    is Response.Success -> _state.value =
                        CreatePostStateUI.Categories(response.data)
                }
            }
    }

    fun createPost(post: CreatePostFields){
        println("create")
        if(!validateFields(post)) return
        if(!isPhotosLoaded()){
            _state.postValue(CreatePostStateUI.Error(R.string.photos_not_loaded))
            return
        }
        val createPost = PostRequest(
            title = post.title,
            text = post.text,
            categoryId = post.category!!.id,
            images = photosAdapter.getPhotosNameList()
        )
        viewModelScope.launch {
            createPostUseCase.execute(createPost)
                .catch {
                    _state.value = CreatePostStateUI.Error(R.string.error_try_again)
                }
                .collect { response ->
                    when(response){
                        is Response.Error -> _state.value = CreatePostStateUI.Error(R.string.error_try_again)
                        is Response.Success ->  _state.value = CreatePostStateUI.Success(Unit)
                    }
                }
        }
    }

    private fun validateFields(post: CreatePostFields): Boolean {
        var result = true
        errorTitle(post.title)?.let { error ->
            _state.value = CreatePostStateUI.ErrorTitle(error)
            result = false }
        errorText(post.text)?.let { error ->
            _state.value = CreatePostStateUI.ErrorText(error)
            result = false }
        errorCategory(post.category)?.let { error ->
            _state.value = CreatePostStateUI.ErrorCategory(error)
            result = false }
        return result
    }

    private fun errorTitle(title: String): Int? {
        return when{
            title.isEmpty() -> R.string.empty_fields
            title.length >= 200 -> R.string.long_title
            else -> null
        }
    }

    private fun errorText(text: String): Int? {
        return when{
            text.isEmpty() -> R.string.empty_fields
            else -> null
        }
    }

    private fun errorCategory(category: CategoryResponse?): Int? {
        return when (category) {
            null -> R.string.empty_fields
            else -> null
        }
    }
}

sealed class CreatePostStateUI {
    data class Error(val error: Int): CreatePostStateUI()
    data class ErrorTitle(val error: Int?): CreatePostStateUI()
    data class ErrorText(val error: Int?): CreatePostStateUI()
    data class ErrorCategory(val error: Int?): CreatePostStateUI()
    data class Categories(val categories: List<CategoryResponse>): CreatePostStateUI()
    data class Success(val unit: Unit): CreatePostStateUI()
    data class Photos(val photos: List<SelectedPhotoModel>): CreatePostStateUI()
}