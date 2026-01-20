package prac.blog.domain.user.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import prac.blog.common.response.success.SuccessResponse
import prac.blog.domain.user.dto.UserReq
import prac.blog.domain.user.service.UserService

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
) {

    @PostMapping
    fun save(
        @RequestBody @Valid request: UserReq.SignUp,
    ): ResponseEntity<*> {
        userService.save(request)
        return ResponseEntity.ok(SuccessResponse.ok())
    }

    @GetMapping("/{userId}")
    fun readById(
        @PathVariable userId: Long,
    ): ResponseEntity<*> {
        return ResponseEntity.ok(
            SuccessResponse.from(
                userService.readById(userId)
            )
        )
    }

    @GetMapping
    fun readAll(): ResponseEntity<*> {
        return ResponseEntity.ok(
            SuccessResponse.from(
                userService.readAll()
            )
        )
    }

    @PutMapping("/{userId}")
    fun updateById(
        @PathVariable userId: Long,
        @RequestBody @Valid request: UserReq.Update,
    ): ResponseEntity<*> {
        userService.updateById(userId, request)
        return ResponseEntity.ok(SuccessResponse.ok())
    }

    @DeleteMapping("/{userId}")
    fun deleteById(
        @PathVariable userId: Long,
    ): ResponseEntity<*> {
        userService.deleteById(userId)
        return ResponseEntity.ok(SuccessResponse.ok())
    }
}
