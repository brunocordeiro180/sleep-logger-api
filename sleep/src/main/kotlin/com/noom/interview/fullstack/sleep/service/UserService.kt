package com.noom.interview.fullstack.sleep.service

import com.noom.interview.fullstack.sleep.exception.UserException
import com.noom.interview.fullstack.sleep.model.User
import com.noom.interview.fullstack.sleep.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService (private val userRepository: UserRepository) {
    private fun findById(id: Long) = userRepository.findById(id)

    fun getUser(id: Long) : User {
        return findById(id).orElseThrow{ UserException("User not found") }
    }
}