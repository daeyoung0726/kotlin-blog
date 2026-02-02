package prac.blog.domain.comment.permission

import org.springframework.stereotype.Component
import prac.blog.common.permission.PermissionChecker
import prac.blog.common.permission.ResourceDomain
import prac.blog.common.response.exception.CustomException
import prac.blog.domain.comment.exception.CommentErrorType
import prac.blog.domain.comment.repository.CommentRepository

@Component
class CommentPermissionChecker(
    private val commentRepository: CommentRepository,
) : PermissionChecker {

    override fun supports(domain: ResourceDomain): Boolean = domain == ResourceDomain.COMMENT

    override fun findOwnerUserId(resourceId: Long): Long =
        commentRepository.findById(resourceId)
            .orElseThrow { CustomException(CommentErrorType.NOT_FOUND) }
            .user.id!!
}
