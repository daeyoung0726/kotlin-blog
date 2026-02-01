package prac.blog.security.filter

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.SignatureException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter
import prac.blog.common.response.error.ErrorResponse
import prac.blog.domain.auth.exception.AuthErrorType
import prac.blog.common.response.error.type.BaseErrorType

class JwtExceptionFilter(
    private val objectMapper: ObjectMapper,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: ExpiredJwtException) {
            handleExceptionToken(response, AuthErrorType.ACCESS_TOKEN_EXPIRED)
        } catch (e: MalformedJwtException) {
            handleExceptionToken(response, AuthErrorType.INVALID_ACCESS_TOKEN)
        } catch (e: SignatureException) {
            handleExceptionToken(response, AuthErrorType.INVALID_TOKEN_SIGNATURE)
        } catch (e: JwtException) {
            handleExceptionToken(response, AuthErrorType.UNKNOWN_TOKEN_ERROR)
        }
    }

    /**
     * Jwt 인증 과정 중, 예외가 발생했을 때 예외를 처리하는 메서드
     */
    private fun handleExceptionToken(
        response: HttpServletResponse,
        errorType: BaseErrorType,
    ) {
        val error = ErrorResponse.from(errorType)
        val messageBody = objectMapper.writeValueAsString(error)

        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.writer.write(messageBody)
    }
}