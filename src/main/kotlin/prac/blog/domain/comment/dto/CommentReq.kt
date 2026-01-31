package prac.blog.domain.comment.dto

import jakarta.validation.constraints.NotBlank
import prac.blog.domain.comment.entity.Comment
import prac.blog.domain.post.entity.Post
import prac.blog.domain.user.entity.User

data class CommentReq(
    @field:NotBlank(message = "댓글은 필수입니다.")
    val content: String,
    val parentId: Long?,
) {
    fun toEntity(
        user: User,
        post: Post,
        parent: Comment?,
        depth: Int,
    ): Comment {
        return Comment(
            content = content,
            post = post,
            user = user,
            parent = parent,
            depth = depth
        )
    }
}