package com.noom.interview.fullstack.sleep.service

import com.noom.interview.fullstack.sleep.model.User
import com.noom.interview.fullstack.sleep.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
class UserServiceTest{

    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var userService: UserService

    @Test
    fun `should return user when found`() {
        val user = User()
        `when`(userRepository.findById(1L)).thenReturn(Optional.of(user))

        val result: Optional<User> = userService.findById(1L)

        assertTrue(result.isPresent)
        assertEquals(user, result.get())
    }

    @Test
    fun `should return empty when user not found`() {
        `when`(userRepository.findById(-1L)).thenReturn(Optional.empty())

        val result: Optional<User> = userService.findById(-1L)

        assertTrue(result.isEmpty)
    }
}