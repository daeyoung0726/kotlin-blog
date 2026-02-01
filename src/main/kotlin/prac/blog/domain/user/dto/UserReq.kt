package prac.blog.domain.user.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import prac.blog.domain.user.entity.User

class UserReq {

    data class SignUp(
        @field:NotBlank(message = "username은 필수입니다.")
        @field:Size(min = 4, max = 20, message = "username은 4~20자여야 합니다.")
        val username: String,

        @field:NotBlank(message = "password는 필수입니다.")
        @field:Size(min = 8, max = 50, message = "password는 8자 이상이어야 합니다.")
        val password: String,

        @field:Email(message = "email 형식이 올바르지 않습니다.")
        val email: String,

        @field:NotBlank(message = "nickname은 필수입니다.")
        @field:Size(min = 2, max = 20, message = "nickname은 2~20자여야 합니다.")
        val nickname: String,
    ) {
        fun toEntity(encodedPassword: String): User =
            User(
                username = username,
                password = encodedPassword,
                email = email,
                nickname = nickname
            )
    }

    data class Update(
        @field:Size(min = 8, max = 50, message = "password는 8자 이상이어야 합니다.")
        val password: String,

        @field:Email(message = "email 형식이 올바르지 않습니다.")
        val email: String,

        @field:Size(min = 2, max = 20, message = "nickname은 2~20자여야 합니다.")
        val nickname: String,
    )
}
