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
import prac.blog.domain.comment.entity.Comment
import prac.blog.domain.user.entity.User

@Entity
@Table(
    name = "comment_like",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_comment_like_user_comment", columnNames = ["user_id", "comment_id"])
    ]
)
class CommentLike(
    user: User,
    comment: Comment,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User = user
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    var comment: Comment = comment
        protected set
}