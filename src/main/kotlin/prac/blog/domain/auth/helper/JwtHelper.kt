package prac.blog.domain.auth.helper

import org.springframework.stereotype.Component
import prac.blog.domain.user.entity.User
import prac.blog.security.jwt.provider.JwtProvider

@Component
class JwtHelper(
    private val jwtProvider: JwtProvider,
) {

    fun createToken(user: User): String {
        val userId = user.id!!
        val username = user.username
        val nickname = user.nickname

        return jwtProvider.generateAccessToken(
            username = username,
            nickname = nickname,
            userId = userId
        )
    }
}
