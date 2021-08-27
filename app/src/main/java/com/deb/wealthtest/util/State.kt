package com.deb.wealthtest.util


sealed class State<T>(
val data: T? = null,
val msg: String? = null
) {
    class Success<T>(data: T) : State<T>(data)
    class Loading<T>(data: T? = null) : State<T>(data)
    class Error<T>(msg: String, data: T? = null) : State<T>(data, msg)
}
