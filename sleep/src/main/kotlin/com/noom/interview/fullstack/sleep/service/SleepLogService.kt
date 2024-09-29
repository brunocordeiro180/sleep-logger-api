package com.noom.interview.fullstack.sleep.service

import com.noom.interview.fullstack.sleep.dto.SleepLogRequestDTO
import com.noom.interview.fullstack.sleep.dto.SleepLogResponseDTO
import com.noom.interview.fullstack.sleep.exception.SleepLogException
import com.noom.interview.fullstack.sleep.exception.UserException
import com.noom.interview.fullstack.sleep.model.SleepLog
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository
import com.noom.interview.fullstack.sleep.utils.DateUtils
import org.springframework.stereotype.Service

@Service
class SleepLogService(
    private val sleepLogRepository: SleepLogRepository,
    private val userService: UserService
) {

    fun createSleepLog(sleepLogRequestDTO: SleepLogRequestDTO, userId: Long) : SleepLogResponseDTO{
        val user = userService.getUser(userId)

        var sleepLog = SleepLog.fromDTO(sleepLogRequestDTO, user)
        sleepLog = sleepLogRepository.save(sleepLog)

        return SleepLogResponseDTO.fromSleepLog(sleepLog)
    }

    fun getLastSleepLog(userId: Long) : SleepLogResponseDTO {
        val user = userService.getUser(userId)
        val lastSleepLog = sleepLogRepository.findFirstByUserOrderByIdDesc(user).orElseThrow {
            SleepLogException(
                "No sleep logs found!"
            )
        }

        return SleepLogResponseDTO.fromSleepLog(lastSleepLog)
    }
}