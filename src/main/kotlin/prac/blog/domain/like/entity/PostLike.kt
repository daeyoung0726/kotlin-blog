package prac.blog.domain.like.entity

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import prac.blog.domain.common.BaseEntity
import prac.blog.domain.post.entity.Post
import prac.blog.domain.user.entity.User

@Entity
@Table(
    name = "post_like",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_post_like_user_post", columnNames = ["user_id", "post_id"])
    ]
)
class PostLike(
    user: User,
    post: Post,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User = user
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    var post: Post = post
        protected set
}
