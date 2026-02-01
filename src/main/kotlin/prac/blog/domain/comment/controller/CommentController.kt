package prac.blog.domain.comment.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import prac.blog.common.response.success.SuccessResponse
import prac.blog.domain.comment.dto.CommentReq
import prac.blog.domain.comment.service.CommentService
import prac.blog.security.authentication.CustomUserDetails

@RestController
@RequestMapping("/api/v1")
class CommentController(
    private val commentService: CommentService,
) {

    @PostMapping("/posts/{postId}/comments")
    fun save(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable postId: Long,
        @RequestBody @Valid request: CommentReq,
    ): ResponseEntity<*> {
        commentService.save(userDetails.userId, postId, request)
        return ResponseEntity.ok(SuccessResponse.ok())
    }

    @GetMapping("/posts/{postId}/comments")
    fun readByPostId(
        @AuthenticationPrincipal userDetails: CustomUserDetails?,
        @PathVariable postId: Long,
    ): ResponseEntity<*> {
        return ResponseEntity.ok(
            SuccessResponse.from(
                commentService.readByPostId(userDetails?.userId, postId)
            )
        )
    }

    @PutMapping("/comments/{commentId}")
    fun updateById(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable commentId: Long,
        @RequestBody @Valid request: CommentReq,
    ): ResponseEntity<*> {
        commentService.updateById(userDetails.userId, commentId, request)
        return ResponseEntity.ok(SuccessResponse.ok())
    }

    @DeleteMapping("/comments/{commentId}")
    fun delete(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable commentId: Long,
    ): ResponseEntity<*> {
        commentService.delete(userDetails.userId, commentId)
        return ResponseEntity.ok(SuccessResponse.ok())
    }
}