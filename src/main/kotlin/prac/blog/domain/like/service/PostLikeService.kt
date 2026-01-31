package prac.blog.domain.like.service

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import prac.blog.common.response.exception.CustomException
import prac.blog.domain.like.entity.PostLike
import prac.blog.domain.like.exception.LikeErrorType
import prac.blog.domain.like.repository.PostLikeRepository
import prac.blog.domain.post.exception.PostErrorType
import prac.blog.domain.post.repository.PostRepository
import prac.blog.domain.user.exception.UserErrorType
import prac.blog.domain.user.repository.UserRepository

@Service
class PostLikeService(
    private val postLikeRepository: PostLikeRepository,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
) {

    @Transactional
    fun save(userId: Long, postId: Long) {
        val user = userRepository.findById(userId)
            .orElseThrow { CustomException(UserErrorType.NOT_FOUND) }

        val post = postRepository.findById(postId)
            .orElseThrow { CustomException(PostErrorType.NOT_FOUND) }

        try {
            postLikeRepository.save(PostLike(user, post))
        } catch (e: DataIntegrityViolationException) {
            throw CustomException(LikeErrorType.ALREADY_POST_LIKED)
        }

        postRepository.updateLikeCount(postId, true);
    }

    @Transactional
    fun delete(userId: Long, postId: Long) {
        val deleted = postLikeRepository.deleteByUserIdAndPostId(userId, postId)

        // 삭제 안됨 -> 권한 x
        if (deleted == 0) {
            throw CustomException(LikeErrorType.NOT_POST_LIKED)
        }

        postRepository.updateLikeCount(postId, false)
    }
}