package com.example.diplom.presentation.ui.createpost

import java.io.File

data class SelectedPhotoModel(
    val id: String,
    val file: File,
    var status: PhotoLoadedStatus = PhotoLoadedStatus.LOADING,
    var fileName: String? = null
)

enum class PhotoLoadedStatus{LOADING, LOADED, ERROR}
