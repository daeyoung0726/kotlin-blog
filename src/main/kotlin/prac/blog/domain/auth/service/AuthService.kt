package prac.blog.domain.auth.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import prac.blog.common.response.exception.CustomException
import prac.blog.domain.auth.dto.SignInDto
import prac.blog.domain.auth.dto.TokenDto
import prac.blog.domain.auth.helper.JwtHelper
import prac.blog.domain.user.exception.UserErrorType
import prac.blog.domain.user.repository.UserRepository

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtHelper: JwtHelper,
) {

    @Transactional(readOnly = true)
    fun signIn(signInDto: SignInDto): TokenDto {
        val user = userRepository.findByUsername(signInDto.username)
            ?: throw CustomException(UserErrorType.NOT_FOUND)

        if (!passwordEncoder.matches(signInDto.password, user.password)) {
            throw CustomException(UserErrorType.NOT_FOUND)
        }

        return TokenDto(jwtHelper.createToken(user))
    }
}
