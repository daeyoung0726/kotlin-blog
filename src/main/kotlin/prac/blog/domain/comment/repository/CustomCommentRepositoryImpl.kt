package prac.blog.domain.comment.repository

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import prac.blog.domain.comment.dto.CommentRes
import prac.blog.domain.comment.entity.QComment
import prac.blog.domain.user.entity.QUser

class CustomCommentRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : CustomCommentRepository {

    private val user = QUser.user
    private val comment = QComment.comment

    override fun readByPostId(postId: Long): List<CommentRes> =
        jpaQueryFactory
            .select(
                Projections.constructor(
                    CommentRes::class.java,
                    comment.id,
                    comment.content,
                    user.nickname,
                    user.id
                )
            )
            .from(comment)
            .join(comment.user, user)
            .where(comment.post.id.eq(postId))
            .orderBy(comment.id.asc())
            .fetch()
}