package prac.blog.domain.like.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import prac.blog.common.response.success.SuccessResponse
import prac.blog.domain.like.service.PostLikeService

@RestController
@RequestMapping("/api/v1/posts")
class PostLikeController(
    private val postLikeService: PostLikeService
) {

    @PostMapping("/{postId}/likes")
    fun like(
        @PathVariable postId: Long,
        @RequestParam(name = "userId") userId: Long,
    ): ResponseEntity<*> {

        postLikeService.save(userId, postId)
        return ResponseEntity.ok(SuccessResponse.ok())
    }

    @DeleteMapping("/{postId}/likes")
    fun unlike(
        @PathVariable postId: Long,
        @RequestParam(name = "userId") userId: Long,
    ): ResponseEntity<*> {

        postLikeService.delete(userId, postId)
        return ResponseEntity.ok(SuccessResponse.ok())
    }
}
