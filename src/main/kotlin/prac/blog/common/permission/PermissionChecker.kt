package prac.blog.common.permission

interface PermissionChecker {
    fun supports(domain: ResourceDomain): Boolean

    fun findOwnerUserId(resourceId: Long): Long
}
