package prac.blog.security.filter

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlin.test.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpHeaders
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import prac.blog.common.util.JwtUtil
import prac.blog.security.jwt.provider.JwtProvider

@ExtendWith(MockKExtension::class)
class JwtAuthenticationFilterTest {

    @MockK
    private lateinit var userDetailsService: UserDetailsService

    @MockK
    private lateinit var jwtProvider: JwtProvider

    @InjectMockKs
    private lateinit var jwtAuthenticationFilter: JwtAuthenticationFilter

    @Test
    @DisplayName("Bearer 토큰이 있으면 인증 정보를 SecurityContext에 저장")
    fun authenticateWithBearerToken() {
        // given
        val token = "jwt-token"
        val userId = 1L

        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val filterChain = mockk<FilterChain>(relaxed = true)
        val userDetails = mockk<UserDetails>(relaxed = true)

        request.addHeader(HttpHeaders.AUTHORIZATION, "${JwtUtil.TOKEN_TYPE}$token")

        every { jwtProvider.getUserId(token) } returns userId
        every { userDetailsService.loadUserByUsername(userId.toString()) } returns userDetails

        // when
        jwtAuthenticationFilter.doFilter(request, response, filterChain)

        // then
        verify(exactly = 1) { jwtProvider.getUserId(token) }
        verify(exactly = 1) { userDetailsService.loadUserByUsername(userId.toString()) }

        val authentication = SecurityContextHolder.getContext().authentication
        assertNotNull(authentication)
        assertEquals(userDetails, authentication!!.principal)
    }

    @Test
    @DisplayName("Authorization 헤더가 없으면 인증을 시도 x")
    fun skipAuthenticationWhenAuthorizationHeaderIsMissing() {
        // given
        val request = mockk<HttpServletRequest>(relaxed = true)
        val response = mockk<HttpServletResponse>(relaxed = true)
        val filterChain = mockk<FilterChain>(relaxed = true)

        every { request.getHeader(HttpHeaders.AUTHORIZATION) } returns null

        // when
        jwtAuthenticationFilter.doFilter(request, response, filterChain)

        // then
        assertNull(SecurityContextHolder.getContext().authentication)

        verify(exactly = 1) { filterChain.doFilter(request, response) }
        verify(exactly = 0) { jwtProvider.getUserId(any()) }
        verify(exactly = 0) { userDetailsService.loadUserByUsername(any()) }
    }

}
