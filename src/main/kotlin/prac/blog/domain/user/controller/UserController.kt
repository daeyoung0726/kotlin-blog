package prac.blog.domain.user.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import prac.blog.common.response.success.SuccessResponse
import prac.blog.domain.user.dto.UserReq
import prac.blog.domain.user.service.UserService
import prac.blog.security.authentication.CustomUserDetails

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

    @GetMapping("/me")
    fun readById(
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<*> {
        return ResponseEntity.ok(
            SuccessResponse.from(
                userService.readById(userDetails.userId)
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

    @PutMapping
    fun updateById(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @RequestBody @Valid request: UserReq.Update,
    ): ResponseEntity<*> {
        userService.updateById(userDetails.userId, request)
        return ResponseEntity.ok(SuccessResponse.ok())
    }

    @DeleteMapping
    fun deleteById(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): ResponseEntity<*> {
        userService.deleteById(userDetails.userId)
        return ResponseEntity.ok(SuccessResponse.ok())
    }
}
