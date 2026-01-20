package prac.blog.domain.user.repository

import org.springframework.data.jpa.repository.JpaRepository
import prac.blog.domain.user.entity.User

interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
}
