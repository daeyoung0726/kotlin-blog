package prac.blog.domain.auth.dto

import jakarta.validation.constraints.NotBlank

data class SignInDto(
    @field:NotBlank(message = "username은 필수입니다.")
    val username: String,

    @field:NotBlank(message = "password은 필수입니다.")
    val password: String,
)
