package com.noom.interview.fullstack.sleep.service

import com.noom.interview.fullstack.sleep.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService (private val userRepository: UserRepository) {
    fun findById(id: Long) = userRepository.findById(id)
}