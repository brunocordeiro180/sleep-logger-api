package com.noom.interview.fullstack.sleep.controller

import com.noom.interview.fullstack.sleep.dto.response.SleepLogAvgResponseDTO
import com.noom.interview.fullstack.sleep.dto.request.SleepLogRequestDTO
import com.noom.interview.fullstack.sleep.dto.response.SleepLogResponseDTO
import com.noom.interview.fullstack.sleep.service.SleepLogService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/sleep-log")
class SleepLogController(private val sleepLogService: SleepLogService) {

    @Operation(summary = "Create a new Sleep Log", description = "Create a sleep log for the last night of sleep")
    @PostMapping("/{userId}")
    fun createSleepLog(
        @Valid @RequestBody sleepLogRequestDTO: SleepLogRequestDTO,
        @PathVariable userId: Long
    ): SleepLogResponseDTO {
        return sleepLogService.createSleepLog(sleepLogRequestDTO, userId)
    }

    @Operation(summary = "Get last Sleep Log", description = "Get the sleep log for the last night of sleep")
    @GetMapping("/{userId}/latest")
    fun getLastSleepLog(
        @PathVariable userId: Long
    ): SleepLogResponseDTO {
        return sleepLogService.getLastSleepLog(userId)
    }

    @Operation(summary = "Average of Sleep", description = "Get the average data of sleep log. Minimum 15 days and maximum of 90")
    @GetMapping("/{userId}/average")
    fun getSleepAverage(
        @PathVariable userId: Long,
        @RequestParam days: Long
    ): SleepLogAvgResponseDTO {
        return sleepLogService.getSleepAverage(userId, days)
    }
}