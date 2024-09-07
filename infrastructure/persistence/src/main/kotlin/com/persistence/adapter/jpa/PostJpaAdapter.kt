import com.domain.Post
import com.usecase.port.PostRepository
import org.springframework.stereotype.Component

@Component
class PostJpaAdapter(
    private val postJpaRepository: PostJpaRepository
) : PostRepository {

    override fun save(post: Post) {
        postJpaRepository.save(PostJpaEntity.from(post))
    }
}
