package prac.blog.common.response.error.type

import org.springframework.http.HttpStatus

enum class GlobalErrorType(
    override val status: HttpStatus,
    override val code: String,
    override val message: String,
) : BaseErrorType {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G001", "서버 내부 에러입니다. 관리자에게 문의하세요."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "G002", "유효성 검증에 실패하였습니다."),
    ;
}
