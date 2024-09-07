package com.usecase.usecase

import com.domain.Post
import com.usecase.port.PostRepository
import org.springframework.stereotype.Service

@Service
class CreatePostService(
    private val postRepository: PostRepository
): CreatePost {

    override fun invoke(command: CreatePost.Command): CreatePost.Result {
        val post: Post = Post.create(command.title, command.content)

        postRepository.save(post)
        return CreatePost.Result.Success
    }
}