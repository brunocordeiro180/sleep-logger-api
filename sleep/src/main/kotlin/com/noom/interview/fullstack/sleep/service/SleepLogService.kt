package com.noom.interview.fullstack.sleep.service

import com.noom.interview.fullstack.sleep.dto.response.SleepLogAvgResponseDTO
import com.noom.interview.fullstack.sleep.dto.request.SleepLogRequestDTO
import com.noom.interview.fullstack.sleep.dto.response.SleepLogResponseDTO
import com.noom.interview.fullstack.sleep.exception.SleepLogException
import com.noom.interview.fullstack.sleep.model.SleepLog
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository
import com.noom.interview.fullstack.sleep.utils.DateUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@Service
class SleepLogService(
    private val sleepLogRepository: SleepLogRepository,
    private val userService: UserService
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun createSleepLog(sleepLogRequestDTO: SleepLogRequestDTO, userId: Long) : SleepLogResponseDTO {
        logger.info("-- Creating sleep log request for user $userId --")
        val user = userService.getUser(userId)

        validateSleepLog(sleepLogRequestDTO);
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

        var avgTimeInBedStart : LocalTime? = null
        var avgTimeInBedEnd : LocalTime? = null
        var avgIntervals : Long = 0


        for(sleepLog in sleepLogList){
            val timeInBedStartSeconds = sleepLog.timeInBedStart.toSecondOfDay()
            val timeInBedEndSeconds = sleepLog.timeInBedEnd.toSecondOfDay()

            timeInBedStartTotal += timeInBedStartSeconds
            timeInBedEndTotal += timeInBedEndSeconds
            totalTimeInBedSum += sleepLog.totalTimeInBed

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

    fun validateSleepLog(sleepLogRequestDTO: SleepLogRequestDTO) {
        val timeInBedEnd = sleepLogRequestDTO.timeInBedEnd
        val timeInBedStart = sleepLogRequestDTO.timeInBedStart
        val sleepLogForToday = sleepLogRepository.findFirstBySleepDate(timeInBedEnd.toLocalDate())

        if (timeInBedEnd.toLocalDate() != LocalDate.now()){
            throw SleepLogException("The date of timeInBedEnd must correspond with today's date")
        }

        if(sleepLogForToday.isPresent){
            throw SleepLogException("There is already a sleep log for today")
        }

        if (timeInBedStart.isAfter(timeInBedEnd)
            || timeInBedStart.isEqual(timeInBedEnd)) {
            throw SleepLogException("The timeBedEnd should be after the timeInBedStart")
        }

        if (Duration.between(timeInBedStart, timeInBedEnd).toHours() > 14){
            throw SleepLogException("The maximum amount of hours in bed that can be registered is 14 hours")
        }

        logger.info("--- Validation passed for sleep log creation ---")
    }

}