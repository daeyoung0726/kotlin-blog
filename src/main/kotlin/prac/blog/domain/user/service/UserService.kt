package prac.blog.domain.user.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import prac.blog.common.response.exception.CustomException
import prac.blog.domain.user.dto.UserReq
import prac.blog.domain.user.dto.UserRes
import prac.blog.domain.user.exception.UserErrorType
import prac.blog.domain.user.repository.UserRepository

@Service
class UserService(
    private val userRepository: UserRepository,
) {

    @Transactional
    fun save(userDto: UserReq.SignUp) {
        userRepository.save(userDto.toEntity())
    }

    @Transactional(readOnly = true)
    fun readById(id: Long): UserRes.Detail {
        val user = userRepository.findById(id)
            .orElseThrow {
                CustomException(UserErrorType.NOT_FOUND)
            }

        return UserRes.Detail.from(user)
    }

    @Transactional(readOnly = true)
    fun readAll(): List<UserRes.Summary> {
        return userRepository.findAll()
            .map(UserRes.Summary::from)
    }

    @Transactional
    fun updateById(
        id: Long,
        userDto: UserReq.Update,
    ) {
        val user = userRepository.findById(id)
            .orElseThrow {
                CustomException(UserErrorType.NOT_FOUND)
            }

        user.updateInfo(
            password = userDto.password,
            email = userDto.email,
            nickname = userDto.nickname
        )
    }

    @Transactional
    fun deleteById(id: Long) {
        userRepository.deleteById(id)
    }
}
