package prac.blog.common.response.success.type

import org.springframework.http.HttpStatus

enum class SuccessType(
    val status: HttpStatus,
    val message: String,
) {
    ;

}
