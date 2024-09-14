package com.guilherme.braintappers.domain

typealias RootError = Error

sealed interface Error

sealed interface Result<out D, out E : RootError> {
    data class Success<out D : Any, out E : RootError>(val data: D) : Result<D, E>
    data class Error<out D, out E : RootError>(val error: E) : Result<D, E>
}

sealed interface DataError: Error {
    enum class Response: DataError{
        UNKNOWN
    }
}