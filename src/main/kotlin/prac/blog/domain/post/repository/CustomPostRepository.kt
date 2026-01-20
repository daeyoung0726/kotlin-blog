package prac.blog.domain.post.repository

import prac.blog.domain.post.dto.PostRes

interface CustomPostRepository {
    fun findDetailsById(postId: Long): PostRes.Detail?
    fun findAllSummaries(): List<PostRes.Summary>
}