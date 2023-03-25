package com.example.diplom.data.network.util

sealed class Response<out T> {
    data class Success<T>(val data: T) : Response<T>()
    data class Error(val code: Int, val message: String) : Response<Nothing>()}