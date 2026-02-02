package prac.blog.domain.comment.entity

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
import prac.blog.domain.post.entity.Post
import prac.blog.domain.user.entity.User

@Entity
@Table(name = "comment")
class Comment(
    content: String,
    post: Post,
    user: User,
    parent: Comment?,
    depth: Int,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    @Column(nullable = false)
    var content: String = content
        protected set

    @Column(nullable = false)
    var depth: Int = depth
        protected set

    @Column(name = "like_count", nullable = false)
    var likeCount: Long = 0
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User = user
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    var post: Post = post
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    var parent: Comment? = parent
        protected set

    fun updateInfo(
        content: String,
    ) {
        this.content = content
    }
}
