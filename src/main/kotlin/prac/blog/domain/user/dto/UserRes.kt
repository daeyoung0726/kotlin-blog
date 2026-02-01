package prac.blog.domain.user.dto

import prac.blog.domain.user.entity.User

class UserRes {
    data class Detail(
        val id: Long,
        val username: String,
        val email: String,
        val nickname: String,
    ) {
        companion object {
            fun from(user: User): Detail =
                Detail(
                    id = requireNotNull(user.id) { "User id must not be null" },
                    username = user.username,
                    email = user.email,
                    nickname = user.nickname
                )
        }
    }

    data class Summary(
        val id: Long,
        val username: String,
        val nickname: String,
    ) {
        companion object {
            fun from(user: User): Summary =
                Summary(
                    id = requireNotNull(user.id) { "User id must not be null" },
                    username = user.username,
                    nickname = user.nickname,
                )
        }
    }
}
