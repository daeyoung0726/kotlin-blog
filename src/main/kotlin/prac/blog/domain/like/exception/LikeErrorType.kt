package prac.blog.domain.like.exception

import org.springframework.http.HttpStatus
import prac.blog.common.response.error.type.BaseErrorType

enum class LikeErrorType(
    override val status: HttpStatus,
    override val code: String,
    override val message: String,
) : BaseErrorType {
    ALREADY_POST_LIKED(HttpStatus.CONFLICT, "L001", "이미 좋아요한 게시글입니다."),
    ALREADY_COMMENT_LIKED(HttpStatus.CONFLICT, "L002", "이미 좋아요한 댓글입니다."),
    NOT_POST_LIKED(HttpStatus.NOT_FOUND, "L003", "해당 게시글에 좋아요하지 않았습니다."),
    NOT_COMMENT_LIKED(HttpStatus.NOT_FOUND, "L004", "해당 댓글에 좋아요하지 않았습니다."),
    ;
}