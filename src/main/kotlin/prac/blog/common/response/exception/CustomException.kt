package prac.blog.common.response.exception

import prac.blog.common.response.error.type.BaseErrorType

class CustomException(
    val errorType: BaseErrorType,
) : RuntimeException(errorType.message) {
}