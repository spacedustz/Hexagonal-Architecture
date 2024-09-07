import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PostJpaRepository: JpaRepository<PostJpaEntity, UUID>