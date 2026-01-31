package prac.blog.domain.like.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import prac.blog.common.response.success.SuccessResponse
import prac.blog.domain.like.service.CommentLikeService

@RestController
@RequestMapping("/api/v1/comments")
class CommentLikeController(
    private val commentLikeService: CommentLikeService
) {

    @PostMapping("/{commentId}/likes")
    fun like(
        @PathVariable commentId: Long,
        @RequestParam(name = "userId") userId: Long,
    ): ResponseEntity<*> {

        commentLikeService.save(userId, commentId)
        return ResponseEntity.ok(SuccessResponse.ok())
    }

    @DeleteMapping("/{commentId}/likes")
    fun unlike(
        @PathVariable commentId: Long,
        @RequestParam(name = "userId") userId: Long,
    ): ResponseEntity<*> {

        commentLikeService.delete(userId, commentId)
        return ResponseEntity.ok(SuccessResponse.ok())
    }
}