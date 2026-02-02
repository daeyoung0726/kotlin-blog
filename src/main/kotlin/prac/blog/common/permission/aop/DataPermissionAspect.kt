package prac.blog.common.permission.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import prac.blog.common.permission.CheckDataPermission
import prac.blog.common.permission.PermissionChecker
import prac.blog.common.permission.PermissionId
import prac.blog.common.permission.ResourceDomain
import prac.blog.common.response.exception.CustomException
import prac.blog.domain.comment.exception.CommentErrorType
import prac.blog.domain.post.exception.PostErrorType
import prac.blog.security.authentication.CustomUserDetails

@Aspect
@Component
class DataPermissionAspect(
    private val checkers: List<PermissionChecker>,
) {

    @Around("@annotation(annotation)")
    fun checkPermission(joinPoint: ProceedingJoinPoint, annotation: CheckDataPermission): Any? {
        val resourceId = findResourceId(joinPoint)
        val actorId = currentUserId()

        val checker = checkers.firstOrNull { candidate ->
            candidate.supports(annotation.domain)
        } ?: error("No PermissionChecker for domain=${annotation.domain}")

        val ownerId = checker.findOwnerUserId(resourceId)

        if (ownerId != actorId) {
            throw when (annotation.domain) {
                ResourceDomain.POST -> CustomException(PostErrorType.NO_PERMISSION)
                ResourceDomain.COMMENT -> CustomException(CommentErrorType.NO_PERMISSION)
            }
        }

        return joinPoint.proceed()
    }

    private fun currentUserId(): Long {
        val principal = SecurityContextHolder.getContext().authentication?.principal as CustomUserDetails
        return (principal).userId
    }

    private fun findResourceId(joinPoint: ProceedingJoinPoint): Long {
        val signature = joinPoint.signature as MethodSignature
        val params = signature.method.parameters
        val args = joinPoint.args

        for (i in params.indices) {
            if (params[i].isAnnotationPresent(PermissionId::class.java)) {
                return (args[i] as? Long)
                    ?: error("@PermissionId must be used on Long parameter")
            }
        }

        error("Missing @PermissionId parameter")
    }
}
