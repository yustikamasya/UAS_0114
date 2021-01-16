package com.example.uas_0114.utils

interface ServerCallback {
    fun onSuccess(response: String)

    fun onFailed(response: String)

    fun onFailure(throwable: Throwable)
}