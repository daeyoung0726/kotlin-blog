package prac.blog.security.filter

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.SignatureException
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.FilterChain
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import prac.blog.common.response.error.ErrorResponse
import prac.blog.domain.auth.exception.AuthErrorType

@ExtendWith(MockKExtension::class)
class JwtExceptionFilterTest {

    @MockK
    private lateinit var objectMapper: ObjectMapper

    @InjectMockKs
    private lateinit var jwtExceptionFilter: JwtExceptionFilter

    private val contentType = "application/json;charset=UTF-8"
    private val characterEncoding = "UTF-8"

    @Test
    @DisplayName("ExpiredJwtException 발생 시 401과 에러 바디를 내려준다")
    fun handleExpiredJwtException() {
        // given
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val chain = mockk<FilterChain>()

        every { chain.doFilter(request, response) } throws ExpiredJwtException(null, null, "expired")
        every { objectMapper.writeValueAsString(any()) } answers {
            val error = firstArg<ErrorResponse>()
            """{"code":"${error.code}","message":"${error.message}"}"""
        }

        // when
        jwtExceptionFilter.doFilter(request, response, chain)

        // then
        assertEquals(401, response.status)
        assertEquals(contentType, response.contentType)
        assertEquals(characterEncoding, response.characterEncoding)
        assertTrue(response.contentAsString.contains(AuthErrorType.ACCESS_TOKEN_EXPIRED.code))
        assertTrue(response.contentAsString.contains(AuthErrorType.ACCESS_TOKEN_EXPIRED.message))

        verify(exactly = 1) { chain.doFilter(request, response) }
        verify(exactly = 1) { objectMapper.writeValueAsString(any()) }
    }

    @Test
    @DisplayName("MalformedJwtException 발생 시 401과 에러 바디를 내려준다")
    fun handleMalformedJwtException() {
        // given
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val chain = mockk<FilterChain>()

        every { chain.doFilter(request, response) } throws MalformedJwtException("malformed")
        every { objectMapper.writeValueAsString(any()) } answers {
            val error = firstArg<ErrorResponse>()
            """{"code":"${error.code}","message":"${error.message}"}"""
        }

        // when
        jwtExceptionFilter.doFilter(request, response, chain)

        // then
        assertEquals(401, response.status)
        assertEquals(contentType, response.contentType)
        assertEquals(characterEncoding, response.characterEncoding)
        assertTrue(response.contentAsString.contains(AuthErrorType.INVALID_ACCESS_TOKEN.code))
        assertTrue(response.contentAsString.contains(AuthErrorType.INVALID_ACCESS_TOKEN.message))

        verify(exactly = 1) { chain.doFilter(request, response) }
        verify(exactly = 1) { objectMapper.writeValueAsString(any()) }
    }

    @Test
    @DisplayName("SignatureException 발생 시 401과 에러 바디를 내려준다")
    fun handleSignatureException() {
        // given
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val chain = mockk<FilterChain>()

        every { chain.doFilter(request, response) } throws SignatureException("bad signature")
        every { objectMapper.writeValueAsString(any()) } answers {
            val error = firstArg<ErrorResponse>()
            """{"code":"${error.code}","message":"${error.message}"}"""
        }

        // when
        jwtExceptionFilter.doFilter(request, response, chain)

        // then
        assertEquals(401, response.status)
        assertEquals(contentType, response.contentType)
        assertEquals(characterEncoding, response.characterEncoding)
        assertTrue(response.contentAsString.contains(AuthErrorType.INVALID_TOKEN_SIGNATURE.code))
        assertTrue(response.contentAsString.contains(AuthErrorType.INVALID_TOKEN_SIGNATURE.message))

        verify(exactly = 1) { chain.doFilter(request, response) }
        verify(exactly = 1) { objectMapper.writeValueAsString(any()) }
    }

    @Test
    @DisplayName("JwtException 발생 시 401과 에러 바디를 내려준다")
    fun handleJwtException() {
        // given
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val chain = mockk<FilterChain>()

        every { chain.doFilter(request, response) } throws JwtException("unknown")
        every { objectMapper.writeValueAsString(any()) } answers {
            val error = firstArg<ErrorResponse>()
            """{"code":"${error.code}","message":"${error.message}"}"""
        }

        // when
        jwtExceptionFilter.doFilter(request, response, chain)

        // then
        assertEquals(401, response.status)
        assertEquals(contentType, response.contentType)
        assertEquals(characterEncoding, response.characterEncoding)
        assertTrue(response.contentAsString.contains(AuthErrorType.UNKNOWN_TOKEN_ERROR.code))
        assertTrue(response.contentAsString.contains(AuthErrorType.UNKNOWN_TOKEN_ERROR.message))

        verify(exactly = 1) { chain.doFilter(request, response) }
        verify(exactly = 1) { objectMapper.writeValueAsString(any()) }
    }

    @Test
    @DisplayName("예외가 없으면 체인을 그대로 통과시킨다")
    fun passThroughWhenNoException() {
        // given
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val chain = mockk<FilterChain>(relaxed = true)

        // when
        jwtExceptionFilter.doFilter(request, response, chain)

        // then
        verify(exactly = 1) { chain.doFilter(request, response) }
        verify(exactly = 0) { objectMapper.writeValueAsString(any()) }
    }
}