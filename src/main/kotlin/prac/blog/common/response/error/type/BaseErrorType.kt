package prac.blog.common.response.error.type

import org.springframework.http.HttpStatus

interface BaseErrorType {
    val status: HttpStatus
    val code: String
    val message: String
}
