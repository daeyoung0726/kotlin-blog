package prac.blog.domain.like.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import java.util.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.dao.DataIntegrityViolationException
import prac.blog.common.response.exception.CustomException
import prac.blog.domain.comment.entity.Comment
import prac.blog.domain.comment.exception.CommentErrorType
import prac.blog.domain.comment.repository.CommentRepository
import prac.blog.domain.like.entity.CommentLike
import prac.blog.domain.like.exception.LikeErrorType
import prac.blog.domain.like.repository.CommentLikeRepository
import prac.blog.domain.user.entity.User
import prac.blog.domain.user.exception.UserErrorType
import prac.blog.domain.user.repository.UserRepository

@ExtendWith(MockKExtension::class)
class CommentLikeServiceTest {

    @MockK
    private lateinit var commentLikeRepository: CommentLikeRepository

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var commentRepository: CommentRepository

    @InjectMockKs
    private lateinit var commentLikeService: CommentLikeService

    @Nested
    @DisplayName("댓글 좋아요 저장")
    inner class Save {

        private val userId = 1L
        private val commentId = 1L

        @Test
        @DisplayName("성공")
        fun success() {
            // given
            val user = mockk<User>()
            val comment = mockk<Comment>()
            val commentLike = mockk<CommentLike>()

            every { userRepository.findById(userId) } returns Optional.of(user)
            every { commentRepository.findById(commentId) } returns Optional.of(comment)
            every { commentLikeRepository.save(any()) } returns commentLike
            every { commentRepository.updateLikeCount(commentId, true) } just Runs

            // when
            commentLikeService.save(userId, commentId)

            // then
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 1) { commentRepository.findById(commentId) }
            verify(exactly = 1) { commentLikeRepository.save(any<CommentLike>()) }
            verify(exactly = 1) { commentRepository.updateLikeCount(commentId, true) }
        }

        @Test
        @DisplayName("실패 - 사용자 존재하지 않음")
        fun userNotFound() {
            // given
            every { userRepository.findById(userId) } returns Optional.empty()

            // when
            val exception = assertThrows(CustomException::class.java) {
                commentLikeService.save(userId, commentId)
            }

            // then
            assertEquals(UserErrorType.NOT_FOUND, exception.errorType)
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 0) { commentRepository.findById(any()) }
            verify(exactly = 0) { commentLikeRepository.save(any()) }
            verify(exactly = 0) { commentRepository.updateLikeCount(any(), any()) }
        }

        @Test
        @DisplayName("실패 - 댓글 존재하지 않음")
        fun commentNotFound() {
            // given
            val user = mockk<User>()

            every { userRepository.findById(userId) } returns Optional.of(user)
            every { commentRepository.findById(commentId) } returns Optional.empty()

            // when
            val exception = assertThrows(CustomException::class.java) {
                commentLikeService.save(userId, commentId)
            }

            // then
            assertEquals(CommentErrorType.NOT_FOUND, exception.errorType)
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 1) { commentRepository.findById(commentId) }
            verify(exactly = 0) { commentLikeRepository.save(any()) }
            verify(exactly = 0) { commentRepository.updateLikeCount(any(), any()) }
        }

        @Test
        @DisplayName("실패 - 이미 좋아요 누름 (유니크 충돌)")
        fun alreadyLiked() {
            // given
            val user = mockk<User>()
            val comment = mockk<Comment>()

            every { userRepository.findById(userId) } returns Optional.of(user)
            every { commentRepository.findById(commentId) } returns Optional.of(comment)
            every { commentLikeRepository.save(any()) } throws DataIntegrityViolationException("duplicate")

            // when
            val exception = assertThrows(CustomException::class.java) {
                commentLikeService.save(userId, commentId)
            }

            // then
            assertEquals(LikeErrorType.ALREADY_COMMENT_LIKED, exception.errorType)
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 1) { commentRepository.findById(commentId) }
            verify(exactly = 1) { commentLikeRepository.save(any<CommentLike>()) }
            verify(exactly = 0) { commentRepository.updateLikeCount(any(), any()) }
        }
    }

    @Nested
    @DisplayName("댓글 좋아요 삭제")
    inner class Delete {

        private val userId = 1L
        private val commentId = 1L

        @Test
        @DisplayName("성공")
        fun success() {
            // given
            every { commentLikeRepository.deleteByUserIdAndCommentId(userId, commentId) } returns 1
            every { commentRepository.updateLikeCount(commentId, false) } just Runs

            // when
            commentLikeService.delete(userId, commentId)

            // then
            verify(exactly = 1) {
                commentLikeRepository.deleteByUserIdAndCommentId(userId, commentId)
            }
            verify(exactly = 1) {
                commentRepository.updateLikeCount(commentId, false)
            }
        }

        @Test
        @DisplayName("실패 - 좋아요 누르지 않음")
        fun notLiked() {
            // given
            every { commentLikeRepository.deleteByUserIdAndCommentId(userId, commentId) } returns 0

            // when
            val exception = assertThrows(CustomException::class.java) {
                commentLikeService.delete(userId, commentId)
            }

            // then
            assertEquals(LikeErrorType.NOT_COMMENT_LIKED, exception.errorType)
            verify(exactly = 1) {
                commentLikeRepository.deleteByUserIdAndCommentId(userId, commentId)
            }
            verify(exactly = 0) {
                commentRepository.updateLikeCount(any(), any())
            }
        }
    }
}
