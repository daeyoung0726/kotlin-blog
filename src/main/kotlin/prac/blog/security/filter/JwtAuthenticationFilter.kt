package prac.blog.security.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.filter.OncePerRequestFilter
import prac.blog.common.util.JwtUtil
import prac.blog.common.util.JwtUtil.Companion.TOKEN_TYPE
import prac.blog.security.jwt.provider.JwtProvider

class JwtAuthenticationFilter(
    private val userDetailsService: UserDetailsService,
    private val jwtProvider: JwtProvider,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val authToken = request.getHeader(HttpHeaders.AUTHORIZATION)

        if (authToken.isNullOrBlank() || !authToken.startsWith(TOKEN_TYPE)) {
            filterChain.doFilter(request, response)
            return
        }

        val accessToken = JwtUtil.resolveToken(authToken)

        val auth = getAuthentication(accessToken)
        SecurityContextHolder.getContext().authentication = auth

        filterChain.doFilter(request, response)
    }


    private fun getAuthentication(token: String): Authentication {
        val userId = jwtProvider.getUserId(token).toString()
        val userDetails: UserDetails = userDetailsService.loadUserByUsername(userId)

        return UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
    }
}