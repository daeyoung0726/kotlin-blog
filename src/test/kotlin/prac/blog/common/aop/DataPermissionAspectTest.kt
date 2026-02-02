package prac.blog.common.aop

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.reflect.MethodSignature
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import prac.blog.common.permission.CheckDataPermission
import prac.blog.common.permission.PermissionChecker
import prac.blog.common.permission.PermissionId
import prac.blog.common.permission.ResourceDomain
import prac.blog.common.permission.aop.DataPermissionAspect
import prac.blog.common.response.exception.CustomException
import prac.blog.domain.comment.exception.CommentErrorType
import prac.blog.security.authentication.CustomUserDetails

@ExtendWith(MockKExtension::class)
class DataPermissionAspectTest {

    @MockK
    private lateinit var commentChecker: PermissionChecker

    @MockK
    private lateinit var postChecker: PermissionChecker

    private lateinit var aspect: DataPermissionAspect

    @BeforeEach
    fun setUp() {
        aspect = DataPermissionAspect(checkers = listOf(commentChecker, postChecker))
    }

    private fun setAuth(userId: Long) {
        val auth = UsernamePasswordAuthenticationToken(
            CustomUserDetails(userId = userId, username = "username"),
            null,
            emptyList()
        )
        SecurityContextHolder.getContext().authentication = auth
    }

    private fun joinPointWithPermissionId(id: Long): ProceedingJoinPoint {
        val signature = mockk<MethodSignature>()
        val method = Dummy::class.java.getMethod("deleteById", java.lang.Long.TYPE)

        every { signature.method } returns method

        val joinPoint = mockk<ProceedingJoinPoint>()
        every { joinPoint.signature } returns signature
        every { joinPoint.args } returns arrayOf(id)

        return joinPoint
    }

    class Dummy {
        fun deleteById(@PermissionId id: Long) = Unit
    }

    private val actorId = 1L

    @Test
    @DisplayName("성공 - 게시글 리소스 소유자면 proceed 호출")
    fun postPermissionSuccess() {
        // given
        val postId = 1L

        setAuth(actorId)
        val joinPoint = joinPointWithPermissionId(postId)
        val annotation = mockk<CheckDataPermission>()
        every { annotation.domain } returns ResourceDomain.POST

        every { commentChecker.supports(ResourceDomain.POST) } returns false
        every { postChecker.supports(ResourceDomain.POST) } returns true
        every { postChecker.findOwnerUserId(postId) } returns actorId
        every { joinPoint.proceed() } returns Unit

        // when
        aspect.checkPermission(joinPoint, annotation)

        // then
        verify(exactly = 1) { postChecker.findOwnerUserId(postId) }
        verify(exactly = 1) { joinPoint.proceed() }
    }

    @Test
    @DisplayName("성공 - 댓글 리소스 소유자면 proceed 호출")
    fun commentPermissionSuccess() {
        // given
        val commentId = 1L

        setAuth(actorId)
        val joinPoint = joinPointWithPermissionId(commentId)
        val annotation = mockk<CheckDataPermission>()
        every { annotation.domain } returns ResourceDomain.COMMENT

        every { postChecker.supports(ResourceDomain.POST) } returns false
        every { commentChecker.supports(ResourceDomain.COMMENT) } returns true
        every { commentChecker.findOwnerUserId(commentId) } returns actorId
        every { joinPoint.proceed() } returns Unit

        // when
        aspect.checkPermission(joinPoint, annotation)

        // then
        verify(exactly = 1) { commentChecker.findOwnerUserId(commentId) }
        verify(exactly = 1) { joinPoint.proceed() }
    }

    @Test
    @DisplayName("실패 - 리소스 없으면 NOT_FOUND")
    fun notFound() {
        // given
        val commentId = 1L

        setAuth(actorId)
        val joinPoint = joinPointWithPermissionId(commentId)
        val annotation = mockk<CheckDataPermission>()
        every { annotation.domain } returns ResourceDomain.COMMENT

        every { postChecker.supports(ResourceDomain.POST) } returns false
        every { commentChecker.supports(ResourceDomain.COMMENT) } returns true
        every { commentChecker.findOwnerUserId(commentId) } throws CustomException(CommentErrorType.NOT_FOUND)

        // when
        val exception = assertThrows(CustomException::class.java) {
            aspect.checkPermission(joinPoint, annotation)
        }

        // then
        assertEquals(CommentErrorType.NOT_FOUND, exception.errorType)
        verify(exactly = 1) { commentChecker.findOwnerUserId(commentId) }
        verify(exactly = 0) { joinPoint.proceed() }
    }

    @Test
    @DisplayName("실패 - 리소스에 대해 권한이 없음")
    fun noPermission() {
        // given
        val commentId = 1L

        setAuth(actorId)
        val joinPoint = joinPointWithPermissionId(commentId)
        val annotation = mockk<CheckDataPermission>()
        every { annotation.domain } returns ResourceDomain.COMMENT

        every { postChecker.supports(ResourceDomain.POST) } returns false
        every { commentChecker.supports(ResourceDomain.COMMENT) } returns true
        every { commentChecker.findOwnerUserId(commentId) } returns 999L // 다른 사람
        every { joinPoint.proceed() } returns Unit

        // when
        val exception = assertThrows(CustomException::class.java) {
            aspect.checkPermission(joinPoint, annotation)
        }

        // then
        assertEquals(CommentErrorType.NO_PERMISSION, exception.errorType)
        verify(exactly = 1) { commentChecker.findOwnerUserId(commentId) }
        verify(exactly = 0) { joinPoint.proceed() }
    }
}
