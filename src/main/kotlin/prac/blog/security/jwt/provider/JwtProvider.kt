package prac.blog.security.jwt.provider

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Component
class JwtProvider(
    @Value("\${jwt.secret}") secret: String,
    @Value("\${jwt.access-expiration}") private val accessExpiration: Long,
    @Value("\${jwt.issuer}") private val issuer: String,
) {
    private val secretKey: SecretKey =
        SecretKeySpec(
            secret.toByteArray(StandardCharsets.UTF_8),
            Jwts.SIG.HS256.key().build().algorithm
        )

    fun generateAccessToken(username: String, nickname: String, userId: Long): String {
        return createJwt(createClaims(userId, nickname), username, accessExpiration)
    }

    private fun createClaims(userId: Long, nickname: String): Map<String, Any> =
        hashMapOf(
            "userId" to userId,
            "nickname" to nickname
        )

    private fun createJwt(
        claims: Map<String, Any>,
        subject: String,
        expirationTime: Long,
    ): String {
        val now = System.currentTimeMillis()
        return Jwts.builder()
            .subject(subject)
            .claims(claims)
            .issuer(issuer)
            .issuedAt(Date(now))
            .expiration(Date(now + expirationTime))
            .signWith(secretKey)
            .compact()
    }

    private fun parseClaims(token: String): Claims =
        Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload

    fun getExpiryDate(token: String): LocalDateTime =
        parseClaims(token).expiration.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()

    fun getUsername(token: String): String =
        parseClaims(token).subject

    fun getNickname(token: String): String =
        parseClaims(token).get("nickname", String::class.java)

    fun getUserId(token: String): Long =
        parseClaims(token).get("userId", java.lang.Long::class.java).toLong()

    fun isExpired(token: String): Boolean =
        parseClaims(token).expiration.before(Date())
}
