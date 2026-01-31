package prac.blog.domain.comment.exception

import org.springframework.http.HttpStatus
import prac.blog.common.response.error.type.BaseErrorType

enum class CommentErrorType(
    override val status: HttpStatus,
    override val code: String,
    override val message: String,
) : BaseErrorType {

    NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "존재하지 않는 댓글입니다."),
    NO_PERMISSION(HttpStatus.FORBIDDEN, "C002", "댓글 처리 권한이 없습니다."),
    PARENT_POST_MISMATCH(HttpStatus.BAD_REQUEST, "C003", "부모 댓글이 해당 게시글에 속하지 않습니다."),
    REPLY_DEPTH_EXCEEDED(HttpStatus.BAD_REQUEST, "C004", "댓글은 최대 3단계까지만 작성할 수 있습니다."),
    ;
}