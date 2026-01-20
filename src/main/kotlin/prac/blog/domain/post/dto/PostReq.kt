package prac.blog.domain.post.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import prac.blog.domain.post.entity.Post
import prac.blog.domain.user.entity.User

data class PostReq(
    @field:NotBlank(message = "제목은 필수입니다.")
    @field:Size(max = 50, message = "제목은 50자 이하여야 합니다.")
    val title: String,

    @field:NotBlank(message = "내용은 필수입니다.")
    val content: String,
) {
    fun toEntity(user: User): Post =
        Post(
            title = title,
            content = content,
            user = user
        )
}