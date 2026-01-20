package prac.blog.domain.comment.repository

import org.springframework.data.jpa.repository.JpaRepository
import prac.blog.domain.comment.entity.Comment

interface CommentRepository : JpaRepository<Comment, Long>, CustomCommentRepository {
}