package com.example.diplom.presentation.common

import android.content.Context
import android.widget.Toast

fun Context.showToast(message: String){
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.showToast(message: Int){
    Toast.makeText(this, getString(message), Toast.LENGTH_LONG).show()
}