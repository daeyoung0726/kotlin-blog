package prac.blog.domain.post.service

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
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import prac.blog.common.response.exception.CustomException
import prac.blog.domain.post.dto.PostReq
import prac.blog.domain.post.dto.PostRes
import prac.blog.domain.post.entity.Post
import prac.blog.domain.post.exception.PostErrorType
import prac.blog.domain.post.repository.PostRepository
import prac.blog.domain.user.entity.User
import prac.blog.domain.user.exception.UserErrorType
import prac.blog.domain.user.repository.UserRepository

@ExtendWith(MockKExtension::class)
class PostServiceTest {

    @MockK
    private lateinit var postRepository: PostRepository

    @MockK
    private lateinit var userRepository: UserRepository

    @InjectMockKs
    private lateinit var postService: PostService

    @Nested
    @DisplayName("저장")
    inner class Save {

        private val userId = 1L

        @Test
        @DisplayName("성공")
        fun save() {
            // given
            val dto = mockk<PostReq>()
            val user = mockk<User>()
            val post = mockk<Post>()

            every { userRepository.findById(userId) } returns Optional.of(user)
            every { dto.toEntity(user) } returns post
            every { postRepository.save(post) } returns post

            // when
            postService.save(userId, dto)

            // then
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 1) { dto.toEntity(user) }
            verify(exactly = 1) { postRepository.save(post) }
        }

        @Test
        @DisplayName("실패 - 사용자 존재하지 않음")
        fun userNotFound() {
            val dto = mockk<PostReq>()

            every { userRepository.findById(userId) } returns Optional.empty()

            val exception = assertThrows(CustomException::class.java) {
                postService.save(userId, dto)
            }

            assertEquals(UserErrorType.NOT_FOUND, exception.errorType)
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 0) { postRepository.save(any()) }
        }
    }

    @Nested
    @DisplayName("조회")
    inner class ReadById {

        private val userId = 1L
        private val postId = 1L

        @Test
        @DisplayName("성공")
        fun success() {
            // given
            val dto = mockk<PostRes.Detail>(relaxed = true)

            every { postRepository.findDetailsById(postId, userId) } returns dto

            // when
            val result: PostRes.Detail = postService.readById(userId, postId)

            // then
            assertNotNull(result)
            verify(exactly = 1) { postRepository.findDetailsById(postId, userId) }
        }

        @Test
        @DisplayName("실패 - 게시글 존재하지 않음")
        fun postNotFound() {
            // given
            every { postRepository.findDetailsById(postId, userId) } returns null

            // when
            val exception = assertThrows(CustomException::class.java) {
                postService.readById(userId, postId)
            }

            // then
            assertEquals(PostErrorType.NOT_FOUND, exception.errorType)
            verify(exactly = 1) { postRepository.findDetailsById(postId, userId) }
        }
    }

    @Nested
    @DisplayName("전체 조회")
    inner class ReadAll {

        @Test
        @DisplayName("성공")
        fun success() {
            // given
            val posts = listOf(
                mockk<PostRes.Summary>(relaxed = true),
                mockk<PostRes.Summary>(relaxed = true)
            )

            every { postRepository.findAllSummaries() } returns posts

            // when
            val result: List<PostRes.Summary> = postService.readAll()

            // then
            assertEquals(posts.size, result.size)
            verify(exactly = 1) { postRepository.findAllSummaries() }
        }
    }

    @Nested
    @DisplayName("수정")
    inner class UpdateById {

        private val userId = 1L
        private val postId = 1L

        @Test
        @DisplayName("성공")
        fun success() {
            // given
            val post = mockk<Post>()
            val dto = PostReq(
                title = "new title",
                content = "new content"
            )

            every { postRepository.findById(postId) } returns Optional.of(post)
            every { post.isOwner(userId) } returns true
            every { post.updateInfo(dto.title, dto.content) } just Runs

            // when
            postService.updateById(userId, postId, dto)

            // then
            verify(exactly = 1) { postRepository.findById(postId) }
            verify(exactly = 1) { post.isOwner(userId) }
            verify(exactly = 1) { post.updateInfo(dto.title, dto.content) }
        }

        @Test
        @DisplayName("실패 - 게시글이 존재하지 않음")
        fun postNotFound() {
            // given
            val dto = mockk<PostReq>()

            every { postRepository.findById(postId) } returns Optional.empty()

            // when
            val exception = assertThrows(CustomException::class.java) {
                postService.updateById(userId, postId, dto)
            }

            // then
            assertEquals(PostErrorType.NOT_FOUND, exception.errorType)
            verify(exactly = 1) { postRepository.findById(postId) }
        }

        @Test
        @DisplayName("실패 - 작성자가 아님")
        fun noPermission() {
            // given
            val dto = mockk<PostReq>()
            val post = mockk<Post>()

            every { postRepository.findById(postId) } returns Optional.of(post)
            every { post.isOwner(userId) } returns false

            // when
            val exception = assertThrows(CustomException::class.java) {
                postService.updateById(userId, postId, dto)
            }

            // then
            assertEquals(PostErrorType.NO_PERMISSION, exception.errorType)
            verify(exactly = 1) { postRepository.findById(postId) }
            verify(exactly = 1) { post.isOwner(userId) }
            verify(exactly = 0) { post.updateInfo(any(), any()) }
        }
    }

    @Nested
    @DisplayName("삭제")
    inner class Delete {

        private val userId = 1L
        private val postId = 1L

        @Test
        @DisplayName("성공")
        fun success() {
            // given
            val post = mockk<Post>()

            every { postRepository.findById(postId) } returns Optional.of(post)
            every { post.isOwner(userId) } returns true
            every { postRepository.delete(post) } just Runs

            // when
            postService.delete(userId, postId)

            // then
            verify(exactly = 1) { postRepository.findById(postId) }
            verify(exactly = 1) { post.isOwner(userId) }
            verify(exactly = 1) { postRepository.delete(post) }
        }

        @Test
        @DisplayName("실패 - 게시글이 존재하지 않음")
        fun postNotFound() {
            // given
            every { postRepository.findById(postId) } returns Optional.empty()

            // when
            val exception = assertThrows(CustomException::class.java) {
                postService.delete(userId, postId)
            }

            // then
            assertEquals(PostErrorType.NOT_FOUND, exception.errorType)
            verify(exactly = 0) { postRepository.delete(any()) }
        }

        @Test
        @DisplayName("실패 - 작성자가 아님")
        fun noPermission() {
            // given
            val post = mockk<Post>()

            every { postRepository.findById(postId) } returns Optional.of(post)
            every { post.isOwner(userId) } returns false

            // when
            val exception = assertThrows(CustomException::class.java) {
                postService.delete(userId, postId)
            }

            // then
            assertEquals(PostErrorType.NO_PERMISSION, exception.errorType)
            verify(exactly = 1) { postRepository.findById(postId) }
            verify(exactly = 1) { post.isOwner(userId) }
            verify(exactly = 0) { postRepository.delete(any()) }
        }
    }

}
