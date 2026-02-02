package prac.blog.domain.post.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import prac.blog.common.permission.CheckDataPermission
import prac.blog.common.permission.PermissionId
import prac.blog.common.permission.ResourceDomain
import prac.blog.common.response.exception.CustomException
import prac.blog.domain.post.dto.PostReq
import prac.blog.domain.post.dto.PostRes
import prac.blog.domain.post.exception.PostErrorType
import prac.blog.domain.post.repository.PostRepository
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
    @CheckDataPermission(domain = ResourceDomain.POST)
    fun updateById(
        @PermissionId postId: Long,
        postDto: PostReq,
    ) {
        val post = postRepository.findById(postId)
            .orElseThrow { CustomException(PostErrorType.NOT_FOUND) }

        post.updateInfo(
            title = postDto.title,
            content = postDto.content
        )
    }

    @Transactional
    @CheckDataPermission(domain = ResourceDomain.POST)
    fun deleteById(
        @PermissionId postId: Long,
    ) {
        postRepository.deleteById(postId)
    }

}