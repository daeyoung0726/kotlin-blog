package prac.blog.domain.post.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import prac.blog.common.response.exception.CustomException
import prac.blog.domain.post.dto.PostReq
import prac.blog.domain.post.dto.PostRes
import prac.blog.domain.post.repository.PostRepository
import prac.blog.domain.post.exception.PostErrorType
import prac.blog.domain.user.exception.UserErrorType
import prac.blog.domain.user.repository.UserRepository

@Service
class PostService(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
) {

    @Transactional
    fun save(userId: Long, postDto: PostReq) {
        val user = userRepository.findById(userId)
            .orElseThrow { CustomException(UserErrorType.NOT_FOUND) }

        postRepository.save(postDto.toEntity(user))
    }

    @Transactional(readOnly = true)
    fun readById(userId: Long?, postId: Long): PostRes.Detail {
        return postRepository.findDetailsById(postId, userId)
            ?: throw CustomException(PostErrorType.NOT_FOUND)
    }

    @Transactional(readOnly = true)
    fun readAll(): List<PostRes.Summary> {
        return postRepository.findAllSummaries()
    }

    @Transactional
    fun updateById(
        userId: Long,
        postId: Long,
        postDto: PostReq,
    ) {
        val post = postRepository.findById(postId)
            .orElseThrow { CustomException(PostErrorType.NOT_FOUND) }

        if (!post.isOwner(userId)) {
            throw CustomException(PostErrorType.NO_PERMISSION)
        }

        post.updateInfo(
            title = postDto.title,
            content = postDto.content
        )
    }

    @Transactional
    fun delete(
        userId: Long,
        postId: Long,
    ) {
        val post = postRepository.findById(postId)
            .orElseThrow { CustomException(PostErrorType.NOT_FOUND) }

        if (!post.isOwner(userId)) {
            throw CustomException(PostErrorType.NO_PERMISSION)
        }

        postRepository.delete(post)
    }

}