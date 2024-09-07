import com.domain.Post
import jakarta.persistence.*
import java.util.*

@Entity
class PostJpaEntity(
    id: UUID,
    title: String,
    content: String
) {
    @Id
    @Column(nullable = false)
    var id: UUID = id
    private set

    @Column(nullable = false)
    var title: String = title
    private set

    @Column(nullable = false)
    var content: String = content
    private set

    companion object {
        fun from(post: Post): PostJpaEntity {
            return PostJpaEntity(post.id, post.title, post.content)
        }
    }
}