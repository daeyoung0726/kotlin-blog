package prac.blog.domain.auth.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import prac.blog.common.response.success.SuccessResponse
import prac.blog.domain.auth.dto.SignInDto
import prac.blog.domain.auth.service.AuthService

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
) {

    @PostMapping("/sign-in")
    fun signIn(@RequestBody @Valid request: SignInDto): ResponseEntity<*> {
        return ResponseEntity.ok(
            SuccessResponse.from(authService.signIn(request))
        )
    }
}