package prac.blog.domain.comment.repository

import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import prac.blog.domain.comment.dto.CommentFlatRes
import prac.blog.domain.comment.dto.CommentRes
import prac.blog.domain.comment.entity.QComment
import prac.blog.domain.like.entity.QCommentLike
import prac.blog.domain.user.entity.QUser

class CustomCommentRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : CustomCommentRepository {

    private val user = QUser.user
    private val comment = QComment.comment
    private val commentLike = QCommentLike.commentLike

    override fun readByPostId(postId: Long, userId: Long?): List<CommentRes> {
        val rows: List<CommentFlatRes> =
            jpaQueryFactory
                .select(
                    Projections.constructor(
                        CommentFlatRes::class.java,
                        comment.id,
                        comment.content,
                        user.nickname,
                        user.id,
                        comment.parent.id,
                        comment.likeCount,
                        commentLike.id.isNotNull
                    )
                )
                .from(comment)
                .join(comment.user, user)
                .leftJoin(commentLike).on(
                    commentLike.comment.id.eq(comment.id)
                        .and(
                            Expressions.booleanTemplate(
                                "{0} IS NOT NULL AND {1} = {0}",
                                userId,
                                commentLike.user.id
                            )
                        )
                )
                .where(comment.post.id.eq(postId))
                .orderBy(comment.id.asc())
                .fetch()

        return buildTree(rows)
    }

    private fun buildTree(rows: List<CommentFlatRes>): List<CommentRes> {
        // id -> 노드
        val nodeMap = HashMap<Long, CommentRes>(rows.size)
        for (row in rows) {
            nodeMap[row.id] = CommentRes(
                id = row.id,
                content = row.content,
                nickname = row.nickname,
                userId = row.userId,
                likeCount = row.likeCount,
                isLiked = row.isLiked,
            )
        }

        // parentId 기준으로 연결
        val roots = mutableListOf<CommentRes>()
        for (row in rows) {
            val node = nodeMap[row.id] ?: continue
            val parentId = row.parentId

            if (parentId == null) {
                roots.add(node)
            } else {
                val parentNode = nodeMap[parentId]
                parentNode?.children?.add(node)
            }
        }

        return roots
    }
}