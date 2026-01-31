package prac.blog.domain.comment.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import prac.blog.domain.comment.entity.Comment

interface CommentRepository : JpaRepository<Comment, Long>, CustomCommentRepository {
    @Modifying
    @Query(
        value = """
            UPDATE comment
            SET like_count = like_count + CASE WHEN :likeStatus THEN 1 ELSE -1 END
            WHERE id = :commentId
                AND (like_count > 0 OR :likeStatus = true)
        """,
        nativeQuery = true
    )
    fun updateLikeCount(commentId: Long, likeStatus: Boolean)
}