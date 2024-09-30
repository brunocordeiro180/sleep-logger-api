package com.noom.interview.fullstack.sleep.service

import com.noom.interview.fullstack.sleep.dto.SleepLogAvgResponseDTO
import com.noom.interview.fullstack.sleep.dto.SleepLogRequestDTO
import com.noom.interview.fullstack.sleep.dto.SleepLogResponseDTO
import com.noom.interview.fullstack.sleep.enums.MorningFeelingEnum
import com.noom.interview.fullstack.sleep.exception.SleepLogException
import com.noom.interview.fullstack.sleep.model.SleepLog
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository
import com.noom.interview.fullstack.sleep.utils.DateUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime

@Service
class SleepLogService(
    private val sleepLogRepository: SleepLogRepository,
    private val userService: UserService
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun createSleepLog(sleepLogRequestDTO: SleepLogRequestDTO, userId: Long) : SleepLogResponseDTO{
        logger.info("-- Creating sleep log request for user $userId --")
        val user = userService.getUser(userId)

        var sleepLog = SleepLog.fromDTO(sleepLogRequestDTO, user)
        sleepLog = sleepLogRepository.save(sleepLog)

        logger.info("-- Sleep log saved for user $userId --")
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

    fun getSleepAverage(userId: Long, days: Long) : SleepLogAvgResponseDTO {
        logger.info("-- Getting sleep average for user $userId --")
        val user = userService.getUser(userId)
        val sleepLogList = sleepLogRepository.findByUserAndSleepDateAfterOrderBySleepDate(user, LocalDate.now().minusDays(days))
        val sleepLogListSize = sleepLogList.size

        validateSleepAvgRequest(sleepLogListSize, days)

        var timeInBedStartTotal = 0
        var timeInBedEndTotal = 0
        var totalTimeInBedSum = 0L
        val mapMorningFeelings = HashMap<String, Int> ()

        MorningFeelingEnum.values().forEach { feeling ->
            mapMorningFeelings[feeling.value] = 0
        }

        var avgTimeInBedStart : LocalTime? = null
        var avgTimeInBedEnd : LocalTime? = null
        var avgIntervals : Long = 0


        for(sleepLog in sleepLogList){
            val timeInBedStartSeconds = sleepLog.timeInBedStart?.toSecondOfDay()
            val timeInBedEndSeconds = sleepLog.timeInBedEnd?.toSecondOfDay()

            timeInBedStartTotal += (timeInBedStartSeconds ?: 0)
            timeInBedEndTotal += (timeInBedEndSeconds ?: 0)
            totalTimeInBedSum += (sleepLog.totalTimeInBed)

            val avgStartSeconds = ((timeInBedStartTotal / sleepLogListSize).toLong())
            val avgEndSeconds = ((timeInBedEndTotal / sleepLogListSize).toLong())

            avgTimeInBedStart = LocalTime.ofSecondOfDay(avgStartSeconds)
            avgTimeInBedEnd = LocalTime.ofSecondOfDay(avgEndSeconds)
            avgIntervals = totalTimeInBedSum/ sleepLogListSize

            sleepLog.morningFeeling?.let { feeling ->
                mapMorningFeelings[feeling.value] = (mapMorningFeelings[feeling.value] ?: 0) + 1
            }
        }
        logger.info("-- Average of sleeps calculated for user $userId --")
        return SleepLogAvgResponseDTO(
            startDate = DateUtils.formatDateWithOrdinal(sleepLogList[0].sleepDate),
            endDate = DateUtils.formatDateWithOrdinal(sleepLogList[sleepLogListSize - 1].sleepDate),
            avgTimeInBedStart = DateUtils.formatTimeTo12HourPeriod(avgTimeInBedStart),
            avgTimeInBedEnd = DateUtils.formatTimeTo12HourPeriod(avgTimeInBedEnd),
            avgTotalTime = DateUtils.formatTimePassed(avgIntervals),
            morningFeelings =  mapMorningFeelings,
        )
    }

    private fun validateSleepAvgRequest(sleepLogListSize: Int, days: Long) {
        if (sleepLogListSize <= 1) {
            throw SleepLogException("There is no enough sleep data to calculate an average!")
        }

        if (days > 90) {
            throw SleepLogException("Maximum interval for average is 90 days")
        }

        if (days < 15) {
            throw SleepLogException("Minimum interval for average is 15 days")
        }
        logger.info("--- Validation passed for avg calculation ---")
    }

}