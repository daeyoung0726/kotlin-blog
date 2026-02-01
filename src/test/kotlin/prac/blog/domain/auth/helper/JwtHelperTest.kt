package prac.blog.domain.auth.helper

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import prac.blog.domain.user.entity.User
import prac.blog.security.jwt.provider.JwtProvider

@ExtendWith(MockKExtension::class)
class JwtHelperTest {

    @MockK
    private lateinit var jwtProvider: JwtProvider

    @InjectMockKs
    private lateinit var jwtHelper: JwtHelper

    @Test
    @DisplayName("토큰 생성 성공")
    fun createTokenSuccess() {
        // given
        val user = mockk<User>()
        val token = "access-token"

        val userId = 1L
        val username = "username"
        val nickname = "nickname"

        every { user.id } returns userId
        every { user.username } returns username
        every { user.nickname } returns nickname

        every {
            jwtProvider.generateAccessToken(
                username = username,
                nickname = nickname,
                userId = userId
            )
        } returns token

        // when
        val result = jwtHelper.createToken(user)

        // then
        assertEquals(token, result)

        verify(exactly = 1) {
            jwtProvider.generateAccessToken(
                username = username,
                nickname = nickname,
                userId = userId
            )
        }
    }
}
