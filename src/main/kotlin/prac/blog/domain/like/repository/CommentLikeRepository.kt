package prac.blog.domain.like.repository

import org.springframework.data.jpa.repository.JpaRepository
import prac.blog.domain.like.entity.CommentLike

interface CommentLikeRepository : JpaRepository<CommentLike, Long> {
    fun deleteByUserIdAndCommentId(userId: Long, commentId: Long): Int
}