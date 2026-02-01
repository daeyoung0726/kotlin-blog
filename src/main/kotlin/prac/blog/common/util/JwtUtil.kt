package prac.blog.common.util

class JwtUtil {
    companion object {
        const val TOKEN_TYPE = "Bearer "

        fun resolveToken(token: String): String =
            token.substring(TOKEN_TYPE.length).trim()
    }
}