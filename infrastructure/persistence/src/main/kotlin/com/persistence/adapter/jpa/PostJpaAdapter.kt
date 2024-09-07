@Component
class PostJpaAdapter(
    private val postJpaRepository: PostJpaRepository
) : PostRepository {

    override fun save(post: Post) {
        postJpaRepository.save(PostJpaEntity.from(post))
    }
}
