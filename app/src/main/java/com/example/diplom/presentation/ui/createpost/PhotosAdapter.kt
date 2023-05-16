package com.example.diplom.presentation.ui.createpost

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.diplom.data.network.util.Response
import com.example.diplom.domain.personinfo.usecase.UploadPhotoUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch

class PhotosAdapter(
    private val uploadPhotoUseCase: UploadPhotoUseCase
) {
    val scope = CoroutineScope(Job() + Dispatchers.IO)

    private val _photos = MutableStateFlow<List<SelectedPhotoModel>>(listOf())
    val photos: StateFlow<List<SelectedPhotoModel>> get() = _photos

    private var photosList = mutableListOf<SelectedPhotoModel>()

    fun addPhoto(photo: SelectedPhotoModel){
        photosList.add(photo)
        uploadPhoto(photo)
    }

    fun reloadPhoto(photo: SelectedPhotoModel){
        uploadPhoto(photo)
    }

    fun deletePhoto(id: String){
        photosList.find { it.id == id }?.let {
            it.file.delete()
            photosList.remove(it)
        }
        submitList()
    }

    fun deleteAllPhotos(){
        photosList.forEach {
            it.file.delete()
            photosList.remove(it)
        }
        submitList()
    }

    fun getItemsCount(): Int = photosList.size

    private fun setStatePhoto(id: String, state: PhotoLoadedStatus){
        photosList = photosList.map {
            if(it.id == id) it.status = state
            it
        }.toMutableList()
        submitList()
    }

    private fun setFileName(id: String, name: String) {
        photosList = photosList.map {
            if(it.id == id) it.fileName = name
            it
        }.toMutableList()
    }

    fun isLoadedPhotos(): Boolean {
        var result = true
        photosList.forEach {
            if(it.status != PhotoLoadedStatus.LOADED && it.fileName != null) {
                println("no filename")
                result = false
            }
        }
        return result
    }

    fun getPhotosNameList(): List<String>{
        return photosList.map { it.fileName!! }
    }

    private fun submitList() = scope.launch{
        _photos.emit(photosList.toList())
    }



    private fun uploadPhoto(photo: SelectedPhotoModel) = scope.launch{
        uploadPhotoUseCase.execute(photo.file)
            .catch {
                setStatePhoto(photo.id, PhotoLoadedStatus.ERROR)
            }
            .collect { response ->
                when(response){
                    is Response.Error -> setStatePhoto(photo.id, PhotoLoadedStatus.ERROR)
                    is Response.Success -> {
                        setFileName(photo.id, response.data)
                        setStatePhoto(photo.id, PhotoLoadedStatus.LOADED)
                    }
                }
            }
    }

    fun cleanUp() {
        scope.cancel()
    }

}