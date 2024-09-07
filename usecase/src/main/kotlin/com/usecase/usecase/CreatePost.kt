package com.usecase.usecase

fun interface CreatePost {
    fun invoke(command: Command): Result

    data class Command(val title: String, val content: String)

    sealed class Result {
        data object Success : Result()
        data class Failure(val throwable: Throwable) : Result()
    }
}