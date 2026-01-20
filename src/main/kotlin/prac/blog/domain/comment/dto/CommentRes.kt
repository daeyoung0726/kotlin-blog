package prac.blog.domain.comment.dto

data class CommentRes(
    val id: Long,
    val content: String,
    val nickname: String,
    val userId: Long,
)
