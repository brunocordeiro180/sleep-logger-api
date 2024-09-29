package com.noom.interview.fullstack.sleep.service

import com.noom.interview.fullstack.sleep.dto.SleepLogRequestDTO
import com.noom.interview.fullstack.sleep.dto.SleepLogResponseDTO
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
        val user = userService.findById(userId).orElseThrow{UserException("User not found")}

        var sleepLog = SleepLog.fromDTO(sleepLogRequestDTO, user)
        sleepLog = sleepLogRepository.save(sleepLog)

        return SleepLogResponseDTO(
            sleepDate = DateUtils.formatDateWithOrdinal(sleepLog.sleepDate),
            timeBedStart = DateUtils.formatDateTimeTo12HourPeriod(sleepLog.timeInBedStart),
            timeBedEnd = DateUtils.formatDateTimeTo12HourPeriod(sleepLog.timeInBedEnd),
            totalTimeInBed = DateUtils.formatTimePassed(sleepLog.totalTimeInBed),
            sleepLog.morningFeeling?.value
        )
    }
}