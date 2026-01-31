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
import prac.blog.domain.like.entity.PostLike
import prac.blog.domain.like.exception.LikeErrorType
import prac.blog.domain.like.repository.PostLikeRepository
import prac.blog.domain.post.entity.Post
import prac.blog.domain.post.exception.PostErrorType
import prac.blog.domain.post.repository.PostRepository
import prac.blog.domain.user.entity.User
import prac.blog.domain.user.exception.UserErrorType
import prac.blog.domain.user.repository.UserRepository

@ExtendWith(MockKExtension::class)
class PostLikeServiceTest {

    @MockK
    private lateinit var postLikeRepository: PostLikeRepository

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var postRepository: PostRepository

    @InjectMockKs
    private lateinit var postLikeService: PostLikeService

    @Nested
    @DisplayName("좋아요 저장")
    inner class Save {

        private val userId = 1L
        private val postId = 1L

        @Test
        @DisplayName("성공")
        fun success() {
            // given
            val user = mockk<User>()
            val post = mockk<Post>()
            val postLike = mockk<PostLike>()

            every { userRepository.findById(userId) } returns Optional.of(user)
            every { postRepository.findById(postId) } returns Optional.of(post)
            every { postLikeRepository.save(any()) } returns postLike
            every { postRepository.updateLikeCount(postId, true) } just Runs

            // when
            postLikeService.save(userId, postId)

            // then
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 1) { postRepository.findById(postId) }
            verify(exactly = 1) { postLikeRepository.save(any<PostLike>()) }
            verify(exactly = 1) { postRepository.updateLikeCount(postId, true) }
        }

        @Test
        @DisplayName("실패 - 사용자 존재하지 않음")
        fun userNotFound() {
            // given
            every { userRepository.findById(userId) } returns Optional.empty()

            // when
            val exception = assertThrows(CustomException::class.java) {
                postLikeService.save(userId, postId)
            }

            // then
            assertEquals(UserErrorType.NOT_FOUND, exception.errorType)
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 0) { postRepository.findById(any()) }
            verify(exactly = 0) { postLikeRepository.save(any()) }
            verify(exactly = 0) { postRepository.updateLikeCount(any(), any()) }
        }

        @Test
        @DisplayName("실패 - 게시글 존재하지 않음")
        fun postNotFound() {
            // given
            val user = mockk<User>()

            every { userRepository.findById(userId) } returns Optional.of(user)
            every { postRepository.findById(postId) } returns Optional.empty()

            // when
            val exception = assertThrows(CustomException::class.java) {
                postLikeService.save(userId, postId)
            }

            // then
            assertEquals(PostErrorType.NOT_FOUND, exception.errorType)
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 1) { postRepository.findById(postId) }
            verify(exactly = 0) { postLikeRepository.save(any()) }
            verify(exactly = 0) { postRepository.updateLikeCount(any(), any()) }
        }

        @Test
        @DisplayName("실패 - 이미 좋아요 한 게시글 (유니크 충돌)")
        fun alreadyLiked() {
            // given
            val user = mockk<User>()
            val post = mockk<Post>()

            every { userRepository.findById(userId) } returns Optional.of(user)
            every { postRepository.findById(postId) } returns Optional.of(post)
            every { postLikeRepository.save(any()) } throws DataIntegrityViolationException("duplicate")

            // when
            val exception = assertThrows(CustomException::class.java) {
                postLikeService.save(userId, postId)
            }

            // then
            assertEquals(LikeErrorType.ALREADY_POST_LIKED, exception.errorType)
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 1) { postRepository.findById(postId) }
            verify(exactly = 1) { postLikeRepository.save(any<PostLike>()) }
            verify(exactly = 0) { postRepository.updateLikeCount(any(), any()) }
        }
    }

    @Nested
    @DisplayName("좋아요 삭제")
    inner class Delete {

        private val userId = 1L
        private val postId = 1L

        @Test
        @DisplayName("성공")
        fun success() {
            // given
            every { postLikeRepository.deleteByUserIdAndPostId(userId, postId) } returns 1
            every { postRepository.updateLikeCount(postId, false) } just Runs

            // when
            postLikeService.delete(userId, postId)

            // then
            verify(exactly = 1) { postLikeRepository.deleteByUserIdAndPostId(userId, postId) }
            verify(exactly = 1) { postRepository.updateLikeCount(postId, false) }
        }

        @Test
        @DisplayName("실패 - 좋아요를 누르지 않은 상태")
        fun notLiked() {
            // given
            every { postLikeRepository.deleteByUserIdAndPostId(userId, postId) } returns 0

            // when
            val exception = assertThrows(CustomException::class.java) {
                postLikeService.delete(userId, postId)
            }

            // then
            assertEquals(LikeErrorType.NOT_POST_LIKED, exception.errorType)
            verify(exactly = 1) { postLikeRepository.deleteByUserIdAndPostId(userId, postId) }
            verify(exactly = 0) { postRepository.updateLikeCount(any(), any()) }
        }
    }

}
