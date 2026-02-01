package prac.blog.security.authentication

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import prac.blog.domain.user.repository.UserRepository

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(userId: String): UserDetails =
        userRepository.findById(userId.toLong())
            .map(CustomUserDetails::from)
            .orElseThrow { UsernameNotFoundException("존재하지 않는 사용자입니다.") }
}
