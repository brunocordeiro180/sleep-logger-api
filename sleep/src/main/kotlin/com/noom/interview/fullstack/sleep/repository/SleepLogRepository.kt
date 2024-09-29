package com.noom.interview.fullstack.sleep.repository

import com.noom.interview.fullstack.sleep.model.SleepLog
import com.noom.interview.fullstack.sleep.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface SleepLogRepository : JpaRepository<SleepLog, Long> {
    fun findFirstByUserOrderByIdDesc(user: User) : Optional<SleepLog>
}