package com.noom.interview.fullstack.sleep.controller;

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.noom.interview.fullstack.sleep.dto.SleepLogAvgResponseDTO
import com.noom.interview.fullstack.sleep.dto.SleepLogRequestDTO
import com.noom.interview.fullstack.sleep.dto.SleepLogResponseDTO
import com.noom.interview.fullstack.sleep.enums.MorningFeelingEnum
import com.noom.interview.fullstack.sleep.exception.SleepLogException
import com.noom.interview.fullstack.sleep.service.SleepLogService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalTime

@ExtendWith(MockitoExtension::class)
class SleepLogControllerTest {

    @Mock
    private lateinit var sleepLogService: SleepLogService

    @InjectMocks
    private lateinit var sleepLogController: SleepLogController

    private val objectMapper: ObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(sleepLogController)
            .setControllerAdvice(AdviceController())
            .build()
    }

    @Test
    fun `should create sleep log successfully`(){
        val userId = 1L
        val sleepLogRequestDTO = SleepLogRequestDTO(
            timeInBedStart = LocalTime.now().minusHours(8),
            timeInBedEnd = LocalTime.now(),
            morningFeeling = MorningFeelingEnum.GOOD
        )

        val expectedResponse = SleepLogResponseDTO(
            sleepDate = "September, 29th",
            timeBedStart = "6:00 AM",
            timeBedEnd = "7:00 AM",
            totalTimeInBed = "1 h",
            morningFeeling = "GOOD"
        )

        whenever(sleepLogService.createSleepLog(any(), anyLong())).thenReturn(expectedResponse)

        mockMvc.perform(MockMvcRequestBuilders.post("/sleep-log/$userId")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(sleepLogRequestDTO)))
            .andExpect(status().isOk)
    }

    @Test
    fun `should return 400 when input is invalid`() {
        val userId = 1L
        val invalidRequestDTO = SleepLogRequestDTO(
            timeInBedStart = null,
            timeInBedEnd = LocalTime.now(),
            morningFeeling = MorningFeelingEnum.GOOD
        )

        mockMvc.perform(MockMvcRequestBuilders.post("/sleep-log/$userId")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequestDTO)))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should return last sleep log successfully`() {
        val userId = 1L

        val expectedResponse = SleepLogResponseDTO(
            sleepDate = "September, 29th",
            timeBedStart = "6:00 AM",
            timeBedEnd = "7:00 AM",
            totalTimeInBed = "1 h",
            morningFeeling = "GOOD"
        )

        whenever(sleepLogService.getLastSleepLog(anyLong())).thenReturn(expectedResponse)

        mockMvc.perform(MockMvcRequestBuilders.get("/sleep-log/$userId/latest")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
    }

    @Test
    fun `should return 400 when the last sleep log was not found`() {
        val userId = 1L

        whenever(sleepLogService.getLastSleepLog(anyLong())).thenThrow(SleepLogException("No sleep logs found!"))

        mockMvc.perform(MockMvcRequestBuilders.get("/sleep-log/$userId/latest")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should return sleep averages successfully`() {
        val userId = 1L
        val days = 30L

        // Mocked response from service layer
        val sleepLogAvgResponseDTO = SleepLogAvgResponseDTO(
            startDate = "August, 1st",
            endDate = "August, 30th",
            avgTimeInBedStart = "10:00 PM",
            avgTimeInBedEnd = "6:30 AM",
            avgTotalTime = "8 h 30 m",
            morningFeelings = mapOf("GOOD" to 15, "OK" to 10, "BAD" to 5) as HashMap<String, Int>
        )

        whenever(sleepLogService.getSleepAverage(anyLong(), anyLong())).thenReturn(sleepLogAvgResponseDTO)

        mockMvc.perform(MockMvcRequestBuilders.get("/sleep-log/$userId/average")
            .param("days", days.toString())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
    }
}
