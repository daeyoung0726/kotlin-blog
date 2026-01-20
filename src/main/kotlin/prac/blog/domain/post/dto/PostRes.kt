package prac.blog.domain.post.dto

class PostRes {

    data class Detail(
        val id: Long,
        val title: String,
        val content: String,
        val nickname: String,
        val userId: Long,
    )

    data class Summary(
        val id: Long,
        val title: String,
        val nickname: String,
    )
}