package com.usecase.port

import com.domain.Post

interface PostRepository {
    fun save(post: Post)
}
