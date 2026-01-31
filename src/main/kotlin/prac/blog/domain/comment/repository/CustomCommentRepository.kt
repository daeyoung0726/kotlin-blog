package prac.blog.domain.comment.repository

import prac.blog.domain.comment.dto.CommentRes

interface CustomCommentRepository {
    fun readByPostId(postId: Long, userId: Long?): List<CommentRes>
}