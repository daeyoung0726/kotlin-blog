package prac.blog.domain.comment.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import prac.blog.common.response.success.SuccessResponse
import prac.blog.domain.comment.dto.CommentReq
import prac.blog.domain.comment.service.CommentService

@RestController
@RequestMapping("/api/v1")
class CommentController(
    private val commentService: CommentService,
) {

    @PostMapping("/posts/{postId}/comments")
    fun save(
        @PathVariable postId: Long,
        @RequestBody @Valid request: CommentReq,
        @RequestParam(name = "userId") userId: Long,
    ): ResponseEntity<*> {
        commentService.save(userId, postId, request)
        return ResponseEntity.ok(SuccessResponse.ok())
    }

    @GetMapping("/posts/{postId}/comments")
    fun readByPostId(
        @PathVariable postId: Long,
    ): ResponseEntity<*> {
        return ResponseEntity.ok(
            SuccessResponse.from(
                commentService.readByPostId(postId)
            )
        )
    }

    @PutMapping("/comments/{commentId}")
    fun updateById(
        @PathVariable commentId: Long,
        @RequestBody @Valid request: CommentReq,
        @RequestParam(name = "userId") userId: Long,
    ): ResponseEntity<*> {
        commentService.updateById(userId, commentId, request)
        return ResponseEntity.ok(SuccessResponse.ok())
    }

    @DeleteMapping("/comments/{commentId}")
    fun delete(
        @PathVariable commentId: Long,
        @RequestParam(name = "userId") userId: Long,
    ): ResponseEntity<*> {
        commentService.delete(userId, commentId)
        return ResponseEntity.ok(SuccessResponse.ok())
    }
}