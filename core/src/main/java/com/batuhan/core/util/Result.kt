package com.batuhan.core.util

sealed class Result<out T> {
    data class Success<out R>(val data: R) : Result<R>()
    data class Error(val exception: Exception) : Result<Nothing>()
}