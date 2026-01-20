package prac.blog.common.response.success

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus
import prac.blog.common.response.success.type.SuccessType

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SuccessResponse<T>(
    val code: Int,
    val message: String,
    val data: T? = null,
) {
    companion object {
        fun ok(): SuccessResponse<Unit> =
            SuccessResponse(
                code = HttpStatus.OK.value(),
                message = "요청이 성공하였습니다.",
            )

        fun from(success: SuccessType): SuccessResponse<Unit> =
            SuccessResponse(
                code = success.status.value(),
                message = success.message,
            )

        fun <T> from(data: T): SuccessResponse<T> =
            SuccessResponse(
                code = HttpStatus.OK.value(),
                message = "요청이 성공하였습니다.",
                data = data,
            )

        fun <T> of(success: SuccessType, data: T): SuccessResponse<T> =
            SuccessResponse(
                code = success.status.value(),
                message = success.message,
                data = data,
            )
    }
}
