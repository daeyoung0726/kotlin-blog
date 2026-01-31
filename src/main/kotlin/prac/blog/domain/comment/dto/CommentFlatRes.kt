package prac.blog.domain.comment.dto

data class CommentFlatRes(
    val id: Long,
    val content: String,
    val nickname: String,
    val userId: Long,
    val parentId: Long?,
    val likeCount: Long,
    val isLiked: Boolean,
)