package prac.blog.domain.like.repository

import org.springframework.data.jpa.repository.JpaRepository
import prac.blog.domain.like.entity.PostLike

interface PostLikeRepository : JpaRepository<PostLike, Long> {
    fun deleteByUserIdAndPostId(userId: Long, postId: Long): Int
}