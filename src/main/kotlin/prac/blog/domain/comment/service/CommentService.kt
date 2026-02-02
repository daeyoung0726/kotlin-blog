package prac.blog.domain.comment.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import prac.blog.common.permission.CheckDataPermission
import prac.blog.common.permission.PermissionId
import prac.blog.common.permission.ResourceDomain
import prac.blog.common.response.exception.CustomException
import prac.blog.domain.comment.dto.CommentReq
import prac.blog.domain.comment.dto.CommentRes
import prac.blog.domain.comment.entity.Comment
import prac.blog.domain.comment.exception.CommentErrorType
import prac.blog.domain.comment.repository.CommentRepository
import prac.blog.domain.post.exception.PostErrorType
import prac.blog.domain.post.repository.PostRepository
import prac.blog.domain.user.exception.UserErrorType
import prac.blog.domain.user.repository.UserRepository

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
) {

    @Transactional
    fun save(
        userId: Long,
        postId: Long,
        commentDto: CommentReq,
    ) {
        val user = userRepository.findById(userId)
            .orElseThrow { CustomException(UserErrorType.NOT_FOUND) }

        val post = postRepository.findById(postId)
            .orElseThrow { CustomException(PostErrorType.NOT_FOUND) }

        val parent = getValidParentCommentOrNull(
            postId,
            commentDto.parentId
        )

        val depth = if (parent == null) 1 else parent.depth + 1
        if (depth > 3) {
            throw CustomException(CommentErrorType.REPLY_DEPTH_EXCEEDED)
        }

        commentRepository.save(commentDto.toEntity(user, post, parent, depth))
    }

    private fun getValidParentCommentOrNull(
        postId: Long,
        parentId: Long?,
    ): Comment? {
        if (parentId == null) {
            return null
        }

        val parent = commentRepository.findById(parentId)
            .orElseThrow { CustomException(CommentErrorType.PARENT_NOT_FOUND) }

        if (parent.post.id != postId) {
            throw CustomException(CommentErrorType.PARENT_POST_MISMATCH)
        }

        return parent
    }

    @Transactional(readOnly = true)
    fun readByPostId(
        userId: Long?,
        postId: Long,
    ): List<CommentRes> = commentRepository.readByPostId(postId, userId)

    @Transactional
    @CheckDataPermission(domain = ResourceDomain.COMMENT)
    fun updateById(
        @PermissionId commentId: Long,
        commentDto: CommentReq,
    ) {
        val comment = commentRepository.findById(commentId)
            .orElseThrow { CustomException(CommentErrorType.NOT_FOUND) }

        comment.updateInfo(content = commentDto.content)
    }

    @Transactional
    @CheckDataPermission(domain = ResourceDomain.COMMENT)
    fun deleteById(
        @PermissionId commentId: Long,
    ) {
        commentRepository.deleteById(commentId)
    }
}
