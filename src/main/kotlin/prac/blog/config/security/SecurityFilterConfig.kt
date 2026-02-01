package prac.blog.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.UserDetailsService
import prac.blog.security.filter.JwtAuthenticationFilter
import prac.blog.security.filter.JwtExceptionFilter
import prac.blog.security.jwt.provider.JwtProvider

@Configuration
class SecurityFilterConfig(
    private val userDetailsService: UserDetailsService,
    private val jwtProvider: JwtProvider,
    private val objectMapper: ObjectMapper,
) {

    @Bean
    fun jwtAuthenticationFilter(): JwtAuthenticationFilter =
        JwtAuthenticationFilter(userDetailsService, jwtProvider)

    @Bean
    fun jwtExceptionFilter(): JwtExceptionFilter =
        JwtExceptionFilter(objectMapper)
}
