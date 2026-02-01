package prac.blog.domain.user.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import java.util.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.crypto.password.PasswordEncoder
import prac.blog.common.response.exception.CustomException
import prac.blog.domain.user.dto.UserReq
import prac.blog.domain.user.dto.UserRes
import prac.blog.domain.user.entity.User
import prac.blog.domain.user.exception.UserErrorType
import prac.blog.domain.user.repository.UserRepository

@ExtendWith(MockKExtension::class)
class UserServiceTest {

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var passwordEncoder: PasswordEncoder

    @InjectMockKs
    private lateinit var userService: UserService

    @Nested
    @DisplayName("저장")
    inner class Save {

        @Test
        @DisplayName("성공")
        fun success() {
            // given
            val dto = mockk<UserReq.SignUp>(relaxed = true)
            val user = mockk<User>()

            every { passwordEncoder.encode(any()) } returns "encodedPw"
            every { dto.toEntity("encodedPw") } returns user
            every { userRepository.save(user) } returns user

            // when
            userService.save(dto)

            // then
            verify(exactly = 1) { dto.toEntity(any()) }
            verify(exactly = 1) { userRepository.save(user) }
        }
    }

    @Nested
    @DisplayName("조회")
    inner class ReadById {

        private val id = 1L

        @Test
        @DisplayName("성공")
        fun success() {
            // given
            val user = mockk<User>(relaxed = true)

            every { userRepository.findById(id) } returns Optional.of(user)

            // when
            val result: UserRes.Detail = userService.readById(id)

            // then
            assertNotNull(result)
            verify(exactly = 1) { userRepository.findById(id) }
        }

        @Test
        @DisplayName("실패 - 사용자 존재하지 않음")
        fun userNotFound() {
            // given
            every { userRepository.findById(id) } returns Optional.empty()

            // when
            val exception = assertThrows(CustomException::class.java) {
                userService.readById(id)
            }

            // then
            assertEquals(UserErrorType.NOT_FOUND, exception.errorType)
            verify(exactly = 1) { userRepository.findById(id) }
        }
    }

    @Nested
    @DisplayName("전체 조회")
    inner class ReadAll {

        @Test
        @DisplayName("성공")
        fun success() {
            // given
            val users = listOf(
                mockk<User>(relaxed = true),
                mockk<User>(relaxed = true),
            )

            every { userRepository.findAll() } returns users

            // when
            val result: List<UserRes.Summary> = userService.readAll()

            // then
            assertEquals(users.size, result.size)
            verify(exactly = 1) { userRepository.findAll() }
        }
    }

    @Nested
    @DisplayName("수정")
    inner class UpdateById {

        private val id = 1L

        @Test
        @DisplayName("성공")
        fun success() {
            // given
            val user = mockk<User>()
            val dto = UserReq.Update(
                password = "newPw",
                email = "new@mail.com",
                nickname = "newNick"
            )

            every { userRepository.findById(id) } returns Optional.of(user)
            every { user.updateInfo(dto.password, dto.email, dto.nickname) } just Runs

            // when
            userService.updateById(id, dto)

            // then
            verify(exactly = 1) { userRepository.findById(id) }
            verify(exactly = 1) { user.updateInfo(dto.password, dto.email, dto.nickname) }
        }

        @Test
        @DisplayName("실패 - 사용자 존재하지 않음")
        fun userNotFound() {
            // given
            val dto = mockk<UserReq.Update>()

            every { userRepository.findById(id) } returns Optional.empty()

            // when
            val exception = assertThrows(CustomException::class.java) {
                userService.updateById(id, dto)
            }

            // then
            assertEquals(UserErrorType.NOT_FOUND, exception.errorType)
            verify(exactly = 1) { userRepository.findById(id) }
            verify(exactly = 0) { userRepository.save(any()) }
        }
    }

    @Nested
    @DisplayName("삭제")
    inner class DeleteById {

        @Test
        @DisplayName("성공")
        fun success() {
            // given
            val id = 1L

            every { userRepository.deleteById(id) } just Runs

            // when
            userService.deleteById(id)

            // then
            verify(exactly = 1) { userRepository.deleteById(id) }
        }
    }
}
