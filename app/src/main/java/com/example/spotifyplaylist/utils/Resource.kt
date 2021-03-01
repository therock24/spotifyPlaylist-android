package com.example.spotifyplaylist.utils

/**
 * Wrapper class for handling Web Requests through LiveData.
 *
 * @param data response data
 * @param message extra details about the request
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T): Resource<T>(data)
    class Error<T>(message: String?, data: T? = null): Resource<T>(data,message)
    class Loading<T>: Resource<T>()
}