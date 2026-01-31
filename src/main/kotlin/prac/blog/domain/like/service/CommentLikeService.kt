package prac.blog.domain.like.service

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import prac.blog.common.response.exception.CustomException
import prac.blog.domain.comment.exception.CommentErrorType
import prac.blog.domain.comment.repository.CommentRepository
import prac.blog.domain.like.entity.CommentLike
import prac.blog.domain.like.exception.LikeErrorType
import prac.blog.domain.like.repository.CommentLikeRepository
import prac.blog.domain.user.exception.UserErrorType
import prac.blog.domain.user.repository.UserRepository

@Service
class CommentLikeService(
    private val commentLikeRepository: CommentLikeRepository,
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository,
) {

    @Transactional
    fun save(userId: Long, commentId: Long) {
        val user = userRepository.findById(userId)
            .orElseThrow { CustomException(UserErrorType.NOT_FOUND) }

        val comment = commentRepository.findById(commentId)
            .orElseThrow { CustomException(CommentErrorType.NOT_FOUND) }

        try {
            commentLikeRepository.save(CommentLike(user, comment))
        } catch (e: DataIntegrityViolationException) {
            throw CustomException(LikeErrorType.ALREADY_COMMENT_LIKED)
        }

        commentRepository.updateLikeCount(commentId, true);
    }

    @Transactional
    fun delete(userId: Long, commentId: Long) {
        val deleted = commentLikeRepository.deleteByUserIdAndCommentId(userId, commentId)

        // 삭제 안됨 -> 권한 x
        if (deleted == 0) {
            throw CustomException(LikeErrorType.NOT_COMMENT_LIKED)
        }

        commentRepository.updateLikeCount(commentId, false)
    }
}