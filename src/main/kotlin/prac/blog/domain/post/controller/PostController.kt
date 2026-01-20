package prac.blog.domain.post.controller

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
import prac.blog.domain.post.dto.PostReq
import prac.blog.domain.post.service.PostService

@RestController
@RequestMapping("/api/v1/posts")
class PostController(
    private val postService: PostService,
) {

    @PostMapping
    fun save(
        @RequestBody @Valid request: PostReq,
        @RequestParam(name = "userId") userId: Long,
    ): ResponseEntity<*> {

        postService.save(userId, request)
        return ResponseEntity.ok(SuccessResponse.ok())
    }

    @GetMapping("/{postId}")
    fun readById(
        @PathVariable postId: Long,
    ): ResponseEntity<*> {
        return ResponseEntity.ok(
            SuccessResponse.from(
                postService.readById(postId)
            )
        )
    }

    @GetMapping
    fun readAll(): ResponseEntity<*> {
        return ResponseEntity.ok(
            SuccessResponse.from(
                postService.readAll()
            )
        )
    }

    @PutMapping("/{postId}")
    fun updateById(
        @PathVariable postId: Long,
        @RequestBody @Valid request: PostReq,
        @RequestParam(name = "userId") userId: Long,
    ): ResponseEntity<*> {

        postService.updateById(userId, postId, request)
        return ResponseEntity.ok(SuccessResponse.ok())
    }

    @DeleteMapping("/{postId}")
    fun delete(
        @PathVariable postId: Long,
        @RequestParam(name = "userId") userId: Long,
    ): ResponseEntity<*> {

        postService.delete(userId, postId)
        return ResponseEntity.ok(SuccessResponse.ok())
    }
}
