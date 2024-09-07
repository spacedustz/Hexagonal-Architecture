import com.example.blog.domain.model.Post
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class PostEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,
    val title: String,
    val content: String
) {
    fun toDomainModel(): Post {
        return Post(id, title, content)
    }

    companion object {
        fun fromDomainModel(post: Post): PostEntity {
            return PostEntity(post.id, post.title, post.content)
        }
    }
}