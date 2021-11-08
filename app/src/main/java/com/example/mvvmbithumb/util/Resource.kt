package com.example.mvvmbithumb.util

sealed class Resource<T : Any> {
    class Success<T : Any>(val data: T) : Resource<T>()
    class Error<T : Any>(val message: String) : Resource<T>()
    object Loading : Resource<Nothing>()
}

//sealed class Resource<T>(
//    val data: T? = null,
//    val message: String? = null
//) {
//    class Success<T>(data: T) : Resource<T>(data)
//    class Loading<T>(data: T? = null) : Resource<T>(data)
//    class Error<T>(message: String) : Resource<T>(message = message)
//}