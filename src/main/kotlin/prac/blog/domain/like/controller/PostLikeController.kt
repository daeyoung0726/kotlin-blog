package prac.blog.domain.like.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import prac.blog.common.response.success.SuccessResponse
import prac.blog.domain.like.service.PostLikeService
import prac.blog.security.authentication.CustomUserDetails

@RestController
@RequestMapping("/api/v1/posts")
class PostLikeController(
    private val postLikeService: PostLikeService
) {

    @PostMapping("/{postId}/likes")
    fun like(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable postId: Long,
    ): ResponseEntity<*> {
        postLikeService.save(userDetails.userId, postId)
        return ResponseEntity.ok(SuccessResponse.ok())
    }

    @DeleteMapping("/{postId}/likes")
    fun unlike(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable postId: Long,
    ): ResponseEntity<*> {
        postLikeService.delete(userDetails.userId, postId)
        return ResponseEntity.ok(SuccessResponse.ok())
    }
}
