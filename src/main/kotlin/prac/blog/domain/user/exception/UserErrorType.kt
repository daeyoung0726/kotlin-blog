package prac.blog.domain.user.exception

import org.springframework.http.HttpStatus
import prac.blog.common.response.error.type.BaseErrorType

enum class UserErrorType(
    override val status: HttpStatus,
    override val code: String,
    override val message: String,
) : BaseErrorType {

    NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "존재하지 않는 사용자입니다."),
    ;
}