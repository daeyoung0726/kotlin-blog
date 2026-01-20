package prac.blog.domain.post.repository

import org.springframework.data.jpa.repository.JpaRepository
import prac.blog.domain.post.entity.Post

interface PostRepository : JpaRepository<Post, Long>, CustomPostRepository {
}