package com.noom.interview.fullstack.sleep.controller

import com.noom.interview.fullstack.sleep.dto.SleepLogRequestDTO
import com.noom.interview.fullstack.sleep.dto.SleepLogResponseDTO
import com.noom.interview.fullstack.sleep.service.SleepLogService
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/sleep-log")
class SleepLogController(private val sleepLogService: SleepLogService) {

    @PostMapping("/{userId}")
    fun createSleepLog(
        @Valid @RequestBody sleepLogRequestDTO: SleepLogRequestDTO,
        @PathVariable userId: Long
    ): SleepLogResponseDTO {
        return sleepLogService.createSleepLog(sleepLogRequestDTO, userId)
    }

    @GetMapping("/{userId}/latest")
    fun getLastSleepLog(
        @PathVariable userId: Long
    ): SleepLogResponseDTO {
        return sleepLogService.getLastSleepLog(userId)
    }
}