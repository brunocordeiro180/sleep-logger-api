package com.noom.interview.fullstack.sleep.service

import com.noom.interview.fullstack.sleep.dto.SleepLogRequestDTO
import com.noom.interview.fullstack.sleep.enums.MorningFeelingEnum
import com.noom.interview.fullstack.sleep.exception.SleepLogException
import com.noom.interview.fullstack.sleep.exception.UserException
import com.noom.interview.fullstack.sleep.mock.SleepLogMock
import com.noom.interview.fullstack.sleep.model.SleepLog
import com.noom.interview.fullstack.sleep.model.User
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository
import com.noom.interview.fullstack.sleep.utils.DateUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.time.LocalTime
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
            LocalTime.now().minusHours(8),
            LocalTime.now(),
            MorningFeelingEnum.GOOD
        )

        val sleepLog = SleepLog.fromDTO(sleepLogRequestDTO, user)

        whenever(userService.getUser(1L)).thenReturn(user)
        whenever(sleepLogRepository.save(any<SleepLog>())).thenReturn(sleepLog)

        val result = sleepLogService.createSleepLog(sleepLogRequestDTO, 1L)

        assertEquals(DateUtils.formatDateWithOrdinal(sleepLog.sleepDate), result.sleepDate)
        assertEquals(DateUtils.formatTimeTo12HourPeriod(sleepLog.timeInBedStart), result.timeBedStart)
        assertEquals(DateUtils.formatTimeTo12HourPeriod(sleepLog.timeInBedEnd), result.timeBedEnd)
        assertEquals(DateUtils.formatTimePassed(sleepLog.totalTimeInBed), result.totalTimeInBed)
        assertEquals(sleepLog.morningFeeling?.value, result.morningFeeling)
    }

    @Test
    fun `should throw UserException when user not found`() {
        val sleepLogRequestDTO = SleepLogRequestDTO(
            LocalTime.now().minusHours(8),
            LocalTime.now(),
            MorningFeelingEnum.GOOD
        )

        whenever(userService.getUser(1L)).thenThrow(UserException("User not found"))

        val exception = assertThrows<UserException> {
            sleepLogService.createSleepLog(sleepLogRequestDTO, 1L)
        }

        assertEquals("User not found", exception.message)
    }

    @Test
    fun `should return last sleep log successfully`() {
        val userId = 1L
        val user = User().apply { id = userId }
        val sleepLog = SleepLogMock.getSleepLog()

        whenever(userService.getUser(userId)).thenReturn(user)
        whenever(sleepLogRepository.findFirstByUserOrderByIdDesc(user)).thenReturn(Optional.of(sleepLog))

        val responseDTO = sleepLogService.getLastSleepLog(userId)

        assertEquals("8 h", responseDTO.totalTimeInBed)
        assertEquals("Good", responseDTO.morningFeeling)
    }

    @Test
    fun `should throw exception when no sleep logs found`() {
        val userId = 1L
        val user = User().apply { id = userId }

        whenever(userService.getUser(userId)).thenReturn(user)
        whenever(sleepLogRepository.findFirstByUserOrderByIdDesc(user)).thenReturn(Optional.empty())

        val exception = assertThrows<SleepLogException> {
            sleepLogService.getLastSleepLog(userId)
        }

        assertEquals("No sleep logs found!", exception.message)
    }

    @Test
    fun `should return sleep average successfully`() {
        val user = User()

        whenever(userService.getUser(anyLong())).thenReturn(user)
        whenever(sleepLogRepository.findByUserAndSleepDateAfterOrderBySleepDate(any(), any())).thenReturn(SleepLogMock.getSleepLogList())

        val response = sleepLogService.getSleepAverage(1, 30L)

        assertNotNull(response)
        assertNotNull(response.avgTimeInBedStart)
        assertNotNull(response.avgTimeInBedEnd)
    }

    @Test
    fun `should throw exception if not enough data for average`() {
        whenever(userService.getUser(anyLong())).thenReturn(User())

        whenever(sleepLogRepository.findByUserAndSleepDateAfterOrderBySleepDate(any(), any()))
            .thenReturn(listOf(SleepLogMock.getSleepLog()))

        val exception = assertThrows<SleepLogException> {
            sleepLogService.getSleepAverage(1, 30L)
        }
        assertEquals("There is no enough sleep data to calculate an average!", exception.message)
    }

    @Test
    fun `should throw exception if interval is greater than 90 days`() {
        whenever(userService.getUser(anyLong())).thenReturn(User())
        whenever(sleepLogRepository.findByUserAndSleepDateAfterOrderBySleepDate(any(), any()))
            .thenReturn(SleepLogMock.getSleepLogList())

        val exception = assertThrows<SleepLogException> {
            sleepLogService.getSleepAverage(1, 91L)
        }
        assertEquals("Maximum interval for average is 90 days", exception.message)
    }

    @Test
    fun `should throw exception if interval is less than 15 days`() {
        whenever(userService.getUser(anyLong())).thenReturn(User())
        whenever(sleepLogRepository.findByUserAndSleepDateAfterOrderBySleepDate(any(), any()))
            .thenReturn(SleepLogMock.getSleepLogList())

        val exception = assertThrows<SleepLogException> {
            sleepLogService.getSleepAverage(1, 14L)
        }
        assertEquals("Minimum interval for average is 15 days", exception.message)
    }
}

