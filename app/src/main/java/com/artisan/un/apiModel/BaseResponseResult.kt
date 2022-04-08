package com.artisan.un.apiModel

data class ErrorData(
    val message: String?,
    val statusCode: Int,
    var priority: ErrorPriority
)

enum class ErrorPriority {
    MEDIUM, LOW
}