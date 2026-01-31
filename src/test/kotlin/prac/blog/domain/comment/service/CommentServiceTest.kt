package prac.blog.domain.comment.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import java.util.*
import kotlin.test.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import prac.blog.common.response.exception.CustomException
import prac.blog.domain.comment.dto.CommentReq
import prac.blog.domain.comment.dto.CommentRes
import prac.blog.domain.comment.entity.Comment
import prac.blog.domain.comment.exception.CommentErrorType
import prac.blog.domain.comment.repository.CommentRepository
import prac.blog.domain.post.entity.Post
import prac.blog.domain.post.exception.PostErrorType
import prac.blog.domain.post.repository.PostRepository
import prac.blog.domain.user.entity.User
import prac.blog.domain.user.exception.UserErrorType
import prac.blog.domain.user.repository.UserRepository

@ExtendWith(MockKExtension::class)
class CommentServiceTest {

    @MockK
    private lateinit var commentRepository: CommentRepository

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var postRepository: PostRepository

    @InjectMockKs
    private lateinit var commentService: CommentService

    @Nested
    @DisplayName("저장")
    inner class Save {

        private val userId = 1L
        private val postId = 1L

        @Test
        @DisplayName("성공")
        fun success() {
            // given
            val user = mockk<User>()
            val post = mockk<Post>()
            val dto = mockk<CommentReq>()
            val comment = mockk<Comment>()

            every { userRepository.findById(userId) } returns Optional.of(user)
            every { postRepository.findById(postId) } returns Optional.of(post)
            every { dto.parentId } returns null
            every { dto.toEntity(user, post, null, 1) } returns comment
            every { commentRepository.save(comment) } returns comment

            // when
            commentService.save(userId, postId, dto)

            // then
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 1) { postRepository.findById(postId) }
            verify(exactly = 1) { dto.toEntity(user, post, null, 1) }
            verify(exactly = 1) { commentRepository.save(comment) }
        }

        @Test
        @DisplayName("실패 - 사용자 존재하지 않음")
        fun userNotFound() {
            val dto = mockk<CommentReq>()

            every { userRepository.findById(userId) } returns Optional.empty()

            val exception = assertThrows(CustomException::class.java) {
                commentService.save(userId, postId, dto)
            }

            assertEquals(UserErrorType.NOT_FOUND, exception.errorType)
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 0) { postRepository.findById(postId) }
            verify(exactly = 0) { commentRepository.findById(any()) }
            verify(exactly = 0) { commentRepository.save(any()) }
        }

        @Test
        @DisplayName("실패 - 게시글 존재하지 않음")
        fun postNotFound() {
            // given
            val user = mockk<User>()
            val dto = mockk<CommentReq>()

            every { userRepository.findById(userId) } returns Optional.of(user)
            every { postRepository.findById(postId) } returns Optional.empty()

            /// when
            val exception = assertThrows(CustomException::class.java) {
                commentService.save(userId, postId, dto)
            }

            // then
            assertEquals(PostErrorType.NOT_FOUND, exception.errorType)
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 1) { postRepository.findById(postId) }
            verify(exactly = 0) { commentRepository.findById(any()) }
            verify(exactly = 0) { commentRepository.save(any()) }
        }

        @Test
        @DisplayName("실패 - 대댓글 깊이 초과 (최대 3)")
        fun depthExceeded() {
            // given
            val user = mockk<User>()
            val post = mockk<Post>()
            val parent = mockk<Comment>()
            val dto = mockk<CommentReq>()

            every { userRepository.findById(userId) } returns Optional.of(user)
            every { postRepository.findById(postId) } returns Optional.of(post)
            every { dto.parentId } returns 10L

            every { commentRepository.findById(10L) } returns Optional.of(parent)
            every { parent.post.id } returns postId
            every { parent.depth } returns 3

            // when
            val exception = assertThrows(CustomException::class.java) {
                commentService.save(userId, postId, dto)
            }

            // then
            assertEquals(CommentErrorType.REPLY_DEPTH_EXCEEDED, exception.errorType)
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 1) { postRepository.findById(postId) }
            verify(exactly = 1) { commentRepository.findById(any()) }
            verify(exactly = 0) { commentRepository.save(any()) }
        }
    }

    @Nested
    @DisplayName("게시글 댓글 조회")
    inner class ReadByPostId {

        private val userId = 1L
        private val postId = 1L

        @Test
        @DisplayName("성공")
        fun success() {
            // given
            val dtos = listOf(
                mockk<CommentRes>(relaxed = true),
                mockk<CommentRes>(relaxed = true)
            )

            every { commentRepository.readByPostId(postId, userId) } returns dtos

            // when
            val result = commentService.readByPostId(userId, postId)

            // then
            assertEquals(dtos, result)
            verify(exactly = 1) { commentRepository.readByPostId(postId, userId) }
        }
    }

    @Nested
    @DisplayName("수정")
    inner class UpdateById {

        private val userId = 1L
        private val commentId = 1L

        @Test
        @DisplayName("성공")
        fun success() {
            // given
            val comment = mockk<Comment>()
            val dto = CommentReq("new", 1L)

            every { commentRepository.findById(commentId) } returns Optional.of(comment)
            every { comment.isOwner(userId) } returns true
            every { comment.updateInfo(dto.content) } just Runs

            // when
            commentService.updateById(userId, commentId, dto)

            // then
            verify(exactly = 1) { commentRepository.findById(commentId) }
            verify(exactly = 1) { comment.isOwner(userId) }
            verify(exactly = 1) { comment.updateInfo(dto.content) }
        }

        @Test
        @DisplayName("실패 - 댓글 존재하지 않음")
        fun commentNotFound() {
            // given
            val dto = mockk<CommentReq>()

            every { commentRepository.findById(any()) } returns Optional.empty()

            // when
            val exception = assertThrows(CustomException::class.java) {
                commentService.updateById(userId, commentId, dto)
            }

            // then
            assertEquals(CommentErrorType.NOT_FOUND, exception.errorType)
            verify(exactly = 1) { commentRepository.findById(commentId) }
        }

        @Test
        @DisplayName("실패 - 작성자가 아님")
        fun noPermission() {
            // given
            val comment = mockk<Comment>()
            val dto = mockk<CommentReq>()

            every { commentRepository.findById(commentId) } returns Optional.of(comment)
            every { comment.isOwner(any()) } returns false

            // when
            val exception = assertThrows(CustomException::class.java) {
                commentService.updateById(1L, 1L, dto)
            }

            // then
            assertEquals(CommentErrorType.NO_PERMISSION, exception.errorType)
            verify(exactly = 1) { commentRepository.findById(commentId) }
            verify(exactly = 1) { comment.isOwner(userId) }
            verify(exactly = 0) { comment.updateInfo(dto.content) }
        }
    }

    @Nested
    @DisplayName("삭제")
    inner class Delete {

        private val userId = 1L
        private val commentId = 1L

        @Test
        @DisplayName("성공")
        fun success() {
            // given
            val comment = mockk<Comment>()

            every { commentRepository.findById(any()) } returns Optional.of(comment)
            every { comment.isOwner(any()) } returns true
            every { commentRepository.delete(comment) } just Runs

            // when
            commentService.delete(userId, commentId)

            // then
            verify(exactly = 1) { commentRepository.findById(any()) }
            verify(exactly = 1) { comment.isOwner(any()) }
            verify(exactly = 1) { commentRepository.delete(comment) }
        }

        @Test
        @DisplayName("실패 - 댓글 존재하지 않음")
        fun commentNotFound() {
            // given
            every { commentRepository.findById(any()) } returns Optional.empty()

            // when
            val exception = assertThrows(CustomException::class.java) {
                commentService.delete(userId, commentId)
            }

            // then
            assertEquals(CommentErrorType.NOT_FOUND, exception.errorType)
            verify(exactly = 1) { commentRepository.findById(commentId) }
            verify(exactly = 0) { commentRepository.delete(any()) }
        }

        @Test
        @DisplayName("실패 - 작성자가 아님")
        fun noPermission() {
            // given
            val comment = mockk<Comment>()

            every { commentRepository.findById(commentId) } returns Optional.of(comment)
            every { comment.isOwner(any()) } returns false

            // when
            val exception = assertThrows(CustomException::class.java) {
                commentService.delete(userId, commentId)
            }

            // then
            assertEquals(CommentErrorType.NO_PERMISSION, exception.errorType)
            verify(exactly = 1) { commentRepository.findById(any()) }
            verify(exactly = 1) { comment.isOwner(any()) }
            verify(exactly = 0) { commentRepository.delete(comment) }
        }
    }
}