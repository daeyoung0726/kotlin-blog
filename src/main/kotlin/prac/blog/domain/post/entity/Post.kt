package prac.blog.domain.post.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import prac.blog.domain.common.BaseEntity
import prac.blog.domain.user.entity.User

@Entity
@Table(name = "post")
class Post(
    title: String,
    content: String,
    user: User,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    @Column(length = 50, nullable = false)
    var title = title
        protected set

    @Column(nullable = false)
    var content = content
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user = user
        protected set

    fun updateInfo(
        title: String,
        content: String,
    ) {
        this.title = title
        this.content = content
    }

    fun isOwner(userId: Long): Boolean {
        return this.user.id == userId
    }
}
