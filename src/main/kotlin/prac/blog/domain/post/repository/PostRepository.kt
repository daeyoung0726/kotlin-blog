package prac.blog.domain.post.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import prac.blog.domain.post.entity.Post

interface PostRepository : JpaRepository<Post, Long>, CustomPostRepository {

    @Modifying
    @Query(
        value = """
            UPDATE post
            SET like_count = like_count + CASE WHEN :likeStatus THEN 1 ELSE -1 END
            WHERE id = :postId
                AND (like_count > 0 OR :likeStatus = true)
        """,
        nativeQuery = true
    )
    fun updateLikeCount(postId: Long, likeStatus: Boolean)
}