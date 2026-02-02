package prac.blog.common.permission

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CheckDataPermission(
    val domain: ResourceDomain,
)
