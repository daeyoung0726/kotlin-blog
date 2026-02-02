package prac.blog.domain.post.permission

import org.springframework.stereotype.Component
import prac.blog.common.permission.PermissionChecker
import prac.blog.common.permission.ResourceDomain
import prac.blog.common.response.exception.CustomException
import prac.blog.domain.post.exception.PostErrorType
import prac.blog.domain.post.repository.PostRepository

@Component
class PostPermissionChecker(
    private val postRepository: PostRepository,
) : PermissionChecker {

    override fun supports(domain: ResourceDomain): Boolean = domain == ResourceDomain.POST

    override fun findOwnerUserId(resourceId: Long): Long =
        postRepository.findById(resourceId)
            .orElseThrow { CustomException(PostErrorType.NOT_FOUND) }
            .user.id!!
}
