package prac.blog.domain.user.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import prac.blog.domain.common.BaseEntity

@Entity
@Table(name = "users")
class User(
    username: String,
    password: String,
    email: String,
    nickname: String,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    @Column(nullable = false, unique = true)
    var username: String = username
        protected set

    @Column(nullable = false)
    var password: String = password
        protected set

    @Column(nullable = false)
    var email: String = email
        protected set

    @Column(nullable = false)
    var nickname: String = nickname
        protected set

    fun updateInfo(
        password: String,
        email: String,
        nickname: String,
    ) {
        this.password = password
        this.email = email
        this.nickname = nickname
    }
}
