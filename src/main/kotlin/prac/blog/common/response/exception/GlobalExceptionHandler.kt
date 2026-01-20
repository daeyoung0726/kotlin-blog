package prac.blog.common.response.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import prac.blog.common.response.error.ErrorResponse
import prac.blog.common.response.error.type.GlobalErrorType

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(e: CustomException): ResponseEntity<*> {
        val error = e.errorType
        log.warn("[Business Warning] {}", error.message)

        return ResponseEntity
            .status(error.status.value())
            .body(
                ErrorResponse.from(error = error)
            )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<*> {
        val error = GlobalErrorType.VALIDATION_ERROR
        val errors: Map<String, String> =
            e.bindingResult.fieldErrors.associate { fe: FieldError ->
                fe.field to (fe.defaultMessage ?: "Invalid value")
            }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse.from(
                    error = error,
                    errors = errors
                )
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<*> {
        log.error("[Error Occurred] {}", e.message, e)
        val error = GlobalErrorType.INTERNAL_SERVER_ERROR
        return ResponseEntity
            .status(error.status.value())
            .body(
                ErrorResponse.from(error)
            )
    }
}
