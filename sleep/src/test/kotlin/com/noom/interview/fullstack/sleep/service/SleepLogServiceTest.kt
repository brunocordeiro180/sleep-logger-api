package com.noom.interview.fullstack.sleep.service

import com.noom.interview.fullstack.sleep.dto.SleepLogRequestDTO
import com.noom.interview.fullstack.sleep.dto.SleepLogResponseDTO
import com.noom.interview.fullstack.sleep.enum.MorningFeelingEnum
import com.noom.interview.fullstack.sleep.exception.SleepLogException
import com.noom.interview.fullstack.sleep.exception.UserException
import com.noom.interview.fullstack.sleep.model.SleepLog
import com.noom.interview.fullstack.sleep.model.User
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository
import com.noom.interview.fullstack.sleep.utils.DateUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class SleepLogServiceTest {

    @Mock
    private lateinit var sleepLogRepository: SleepLogRepository

    @Mock
    private lateinit var userService: UserService

    @InjectMocks
    private lateinit var sleepLogService: SleepLogService

    @Test
    fun `should create sleep log successfully`() {

        val user = User()

        val sleepLogRequestDTO = SleepLogRequestDTO(
            timeInBedStart = LocalDateTime.now().minusHours(8),
            timeInBedEnd = LocalDateTime.now(),
            morningFeeling = MorningFeelingEnum.GOOD
        )

        val sleepLog = SleepLog.fromDTO(sleepLogRequestDTO, user)

        `when`(userService.getUser(1L)).thenReturn(user)
        `when`(sleepLogRepository.save(any(SleepLog::class.java))).thenReturn(sleepLog)

        val result: SleepLogResponseDTO = sleepLogService.createSleepLog(sleepLogRequestDTO, 1L)

        assertEquals(DateUtils.formatDateWithOrdinal(sleepLog.sleepDate), result.sleepDate)
        assertEquals(DateUtils.formatDateTimeTo12HourPeriod(sleepLog.timeInBedStart), result.timeBedStart)
        assertEquals(DateUtils.formatDateTimeTo12HourPeriod(sleepLog.timeInBedEnd), result.timeBedEnd)
        assertEquals(DateUtils.formatTimePassed(sleepLog.totalTimeInBed), result.totalTimeInBed)
        assertEquals(sleepLog.morningFeeling?.value, result.morningFeeling)
    }

    @Test
    fun `should throw UserException when user not found`() {
        val sleepLogRequestDTO = SleepLogRequestDTO(
            timeInBedStart = LocalDateTime.now().minusHours(8),
            timeInBedEnd = LocalDateTime.now(),
            morningFeeling = MorningFeelingEnum.GOOD
        )

        `when`(userService.getUser(1L)).thenThrow(UserException("User not found"))

        val exception = assertThrows<UserException> {
            sleepLogService.createSleepLog(sleepLogRequestDTO, 1L)
        }

        assertEquals("User not found", exception.message)
    }

    @Test
    fun `should return last sleep log successfully`() {
        val userId = 1L
        val user = User(id = userId)
        val sleepLog = SleepLog(
            id = 1L,
            user = user,
            timeInBedStart = LocalDateTime.now().minusHours(8),
            timeInBedEnd = LocalDateTime.now(),
            totalTimeInBed = 480,
            morningFeeling = MorningFeelingEnum.GOOD
        )

        `when`(userService.getUser(userId)).thenReturn(user)
        `when`(sleepLogRepository.findFirstByUserOrderByIdDesc(user)).thenReturn(Optional.of(sleepLog))

        val responseDTO = sleepLogService.getLastSleepLog(userId)

        assertEquals("8 h", responseDTO.totalTimeInBed)
        assertEquals("Good", responseDTO.morningFeeling)
    }

    @Test
    fun `should throw exception when no sleep logs found`() {
        val userId = 1L
        val user = User(id = userId)

        `when`(userService.getUser(userId)).thenReturn(user)
        `when`(sleepLogRepository.findFirstByUserOrderByIdDesc(user)).thenReturn(Optional.empty())

        val exception = assertThrows<SleepLogException> {
            sleepLogService.getLastSleepLog(userId)
        }

        assertEquals("No sleep logs found!", exception.message)
    }
}