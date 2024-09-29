package com.noom.interview.fullstack.sleep.controller;

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.noom.interview.fullstack.sleep.dto.SleepLogRequestDTO
import com.noom.interview.fullstack.sleep.dto.SleepLogResponseDTO
import com.noom.interview.fullstack.sleep.enum.MorningFeelingEnum
import com.noom.interview.fullstack.sleep.exception.SleepLogException
import com.noom.interview.fullstack.sleep.service.SleepLogService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDateTime

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
            timeInBedStart = LocalDateTime.now().minusHours(8),
            timeInBedEnd = LocalDateTime.now(),
            morningFeeling = MorningFeelingEnum.GOOD
        )

        val expectedResponse = SleepLogResponseDTO(
            sleepDate = "September, 29th",
            timeBedStart = "6:00 AM",
            timeBedEnd = "7:00 AM",
            totalTimeInBed = "1 h",
            morningFeeling = "GOOD"
        )

        `when`(sleepLogService.createSleepLog(sleepLogRequestDTO, userId)).thenReturn(expectedResponse)

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
            timeInBedEnd = LocalDateTime.now(),
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

        `when`(sleepLogService.getLastSleepLog(userId)).thenReturn(expectedResponse)

        mockMvc.perform(MockMvcRequestBuilders.get("/sleep-log/$userId/latest")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
    }

    @Test
    fun `should return 400 when the last sleep log was not found`() {
        val userId = 1L

        `when`(sleepLogService.getLastSleepLog(userId)).thenThrow(SleepLogException("No sleep logs found!"))

        mockMvc.perform(MockMvcRequestBuilders.get("/sleep-log/$userId/latest")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest)
    }
}
