package prac.blog.domain.auth.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.crypto.password.PasswordEncoder
import prac.blog.common.response.exception.CustomException
import prac.blog.domain.auth.dto.SignInDto
import prac.blog.domain.auth.helper.JwtHelper
import prac.blog.domain.user.entity.User
import prac.blog.domain.user.exception.UserErrorType
import prac.blog.domain.user.repository.UserRepository

@ExtendWith(MockKExtension::class)
class AuthServiceTest {

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var passwordEncoder: PasswordEncoder

    @MockK
    private lateinit var jwtHelper: JwtHelper

    @InjectMockKs
    private lateinit var authService: AuthService

    @Nested
    @DisplayName("로그인")
    inner class SignIn {

        private val username = "test"
        private val password = "password123"

        @Test
        @DisplayName("성공")
        fun success() {
            // given
            val dto = SignInDto(username, password)
            val user = mockk<User>()
            val token = "access-token"
            val encoded = "encoded-password"

            every { userRepository.findByUsername(username) } returns user
            every { user.password } returns encoded
            every { passwordEncoder.matches(password, encoded) } returns true
            every { jwtHelper.createToken(user) } returns token

            // when
            val result = authService.signIn(dto)

            // then
            assertEquals(token, result.accessToken)
            verify(exactly = 1) { userRepository.findByUsername(username) }
            verify(exactly = 1) { passwordEncoder.matches(password, encoded) }
            verify(exactly = 1) { jwtHelper.createToken(user) }
        }

        @Test
        @DisplayName("실패 - 사용자 존재하지 않음")
        fun userNotFound() {
            // given
            val dto = SignInDto(username, password)

            every { userRepository.findByUsername(username) } returns null

            // when
            val exception = assertThrows(CustomException::class.java) {
                authService.signIn(dto)
            }

            // then
            assertEquals(UserErrorType.NOT_FOUND, exception.errorType)
            verify(exactly = 1) { userRepository.findByUsername(username) }
            verify(exactly = 0) { passwordEncoder.matches(any(), any()) }
            verify(exactly = 0) { jwtHelper.createToken(any()) }
        }

        @Test
        @DisplayName("실패 - 비밀번호 불일치")
        fun passwordMismatch() {
            // given
            val dto = SignInDto(username, password)
            val user = mockk<User>()
            val encoded = "encoded-password"

            every { userRepository.findByUsername(username) } returns user
            every { user.password } returns encoded
            every { passwordEncoder.matches(password, encoded) } returns false

            // when
            val exception = assertThrows(CustomException::class.java) {
                authService.signIn(dto)
            }

            // then
            assertEquals(UserErrorType.NOT_FOUND, exception.errorType)
            verify(exactly = 1) { userRepository.findByUsername(username) }
            verify(exactly = 1) { passwordEncoder.matches(password, encoded) }
            verify(exactly = 0) { jwtHelper.createToken(any()) }
        }
    }
}
