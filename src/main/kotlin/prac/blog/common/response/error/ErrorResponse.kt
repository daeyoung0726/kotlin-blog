package prac.blog.common.response.error

import com.fasterxml.jackson.annotation.JsonInclude
import prac.blog.common.response.error.type.BaseErrorType

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponse(
    val code: String,
    val message: String,
    val errors: Map<String, String>? = null,
    val data: Any? = null,
) {
    companion object {
        fun from(
            error: BaseErrorType,
            data: Any? = null,
            errors: Map<String, String>? = null,
        ): ErrorResponse =
            ErrorResponse(
                code = error.code,
                message = error.message,
                data = data,
                errors = errors,
            )
    }
}
