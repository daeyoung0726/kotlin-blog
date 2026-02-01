package prac.blog.security.authentication

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import prac.blog.domain.user.entity.User

@JsonIgnoreProperties(ignoreUnknown = true)
data class CustomUserDetails(
    val userId: Long,
    private val username: String,
) : UserDetails {

    companion object {
        fun from(user: User): CustomUserDetails =
            CustomUserDetails(
                userId = user.id!!,
                username = user.username,
            )
    }

    override fun getAuthorities(): Collection<GrantedAuthority> = emptyList()
    override fun getPassword(): String? = null
    override fun getUsername(): String = username
}
