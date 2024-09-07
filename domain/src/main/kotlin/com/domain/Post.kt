package com.domain

import com.ulid.UlidUtil
import java.util.*

data class Post(
    val id: UUID = UlidUtil.createUlid(),
    val title: String,
    val content: String
) {
    companion object {
        const val LIMIT_COUNT = 100L;
        fun create(title: String, content: String): Post {
            return Post(title = title, content = content)
        }
    }
}