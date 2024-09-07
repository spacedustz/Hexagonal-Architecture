package com.api.adapter.controller

import com.api.adapter.dto.CreatePostRequest
import com.usecase.usecase.CreatePost
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/post")
class PostController(
    private val createPost: CreatePost
) {

    @PostMapping
    fun createPost(@RequestBody request: CreatePostRequest): ResponseEntity<Unit> {
        val command: CreatePost.Command = CreatePost.Command(title = request.title, content = request.content)

        val result = createPost.invoke(command)
        return when (result) {
            is CreatePost.Result.Success -> ResponseEntity.ok().build()
            is CreatePost.Result.Failure -> ResponseEntity.badRequest().build()
        }
    }
}