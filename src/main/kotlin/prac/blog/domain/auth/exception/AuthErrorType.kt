package prac.blog.domain.auth.exception

import org.springframework.http.HttpStatus
import prac.blog.common.response.error.type.BaseErrorType

enum class AuthErrorType(
    override val status: HttpStatus,
    override val code: String,
    override val message: String,
) : BaseErrorType {

    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A001", "Access Token이 만료되었습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "Access Token이 잘못되었습니다."),
    INVALID_TOKEN_SIGNATURE(HttpStatus.UNAUTHORIZED, "A003", "Access Token의 서명이 잘못되었습니다."),
    UNKNOWN_TOKEN_ERROR(HttpStatus.UNAUTHORIZED, "A004", "알 수 없는 토큰 에러입니다."),
    ;

}
