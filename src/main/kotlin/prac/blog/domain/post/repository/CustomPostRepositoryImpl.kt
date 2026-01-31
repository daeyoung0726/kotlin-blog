package prac.blog.domain.post.repository

import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import prac.blog.domain.like.entity.QPostLike
import prac.blog.domain.post.dto.PostRes
import prac.blog.domain.post.entity.QPost
import prac.blog.domain.user.entity.QUser

class CustomPostRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : CustomPostRepository {

    private val user = QUser.user
    private val post = QPost.post
    private val postLike = QPostLike.postLike

    override fun findDetailsById(postId: Long, userId: Long?): PostRes.Detail? {
        val isLiked =
            if (userId == null) {
                Expressions.FALSE
            } else {
                JPAExpressions
                    .selectOne()
                    .from(postLike)
                    .where(
                        postLike.post.id.eq(post.id),
                        postLike.user.id.eq(userId)
                    )
                    .exists()
            }

        return jpaQueryFactory
            .select(
                Projections.constructor(
                    PostRes.Detail::class.java,
                    post.id,
                    post.title,
                    post.content,
                    user.nickname,
                    user.id,
                    post.likeCount,
                    isLiked
                )
            )
            .from(post)
            .join(post.user, user)
            .where(post.id.eq(postId))
            .fetchOne()
    }

    override fun findAllSummaries(): List<PostRes.Summary> =
        jpaQueryFactory
            .select(
                Projections.constructor(
                    PostRes.Summary::class.java,
                    post.id,
                    post.title,
                    user.nickname,
                    post.likeCount
                )
            )
            .from(post)
            .join(post.user, user)
            .orderBy(post.id.desc())
            .fetch()
}
