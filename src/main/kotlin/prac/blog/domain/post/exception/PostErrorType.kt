package prac.blog.domain.post.exception

import org.springframework.http.HttpStatus
import prac.blog.common.response.error.type.BaseErrorType

enum class PostErrorType(
    override val status: HttpStatus,
    override val code: String,
    override val message: String,
) : BaseErrorType {

    NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "존재하지 않는 게시글입니다."),
    NO_PERMISSION(HttpStatus.FORBIDDEN, "P002", "게시글 처리 권한이 없습니다."),
    ;
}