package com.marin.fireexpense.vo

/**
 * Created by Enrique Marín on 02-10-2020.
 */

sealed class Result<out T> {
    class Loading<out T> : Result<T>()
    data class Success<out T>(val data: T) : Result<T>()
    data class Failure<out T>(val exception: Throwable) : Result<T>()
}