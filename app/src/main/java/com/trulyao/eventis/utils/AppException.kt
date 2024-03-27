package com.trulyao.eventis.utils

import android.content.Context
import com.trulyao.northlearn.utils.Alert

class AppException : Exception {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}

public fun handleException(context: Context, e: Exception) {
    val message = when (e) {
        is AppException -> e.message
        else -> {
            System.err.println(e.message)
            System.err.println(e.stackTrace)
            "Something went wrong, see logs for more details"
        }
    }

    Alert.show(context, message!!)
}