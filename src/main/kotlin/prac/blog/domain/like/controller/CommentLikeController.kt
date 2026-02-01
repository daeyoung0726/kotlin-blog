package prac.blog.domain.like.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import prac.blog.common.response.success.SuccessResponse
import prac.blog.domain.like.service.CommentLikeService
import prac.blog.security.authentication.CustomUserDetails

@RestController
@RequestMapping("/api/v1/comments")
class CommentLikeController(
    private val commentLikeService: CommentLikeService
) {

    @PostMapping("/{commentId}/likes")
    fun like(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable commentId: Long,
    ): ResponseEntity<*> {
        commentLikeService.save(userDetails.userId, commentId)
        return ResponseEntity.ok(SuccessResponse.ok())
    }

    @DeleteMapping("/{commentId}/likes")
    fun unlike(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable commentId: Long,
    ): ResponseEntity<*> {
        commentLikeService.delete(userDetails.userId, commentId)
        return ResponseEntity.ok(SuccessResponse.ok())
    }
}