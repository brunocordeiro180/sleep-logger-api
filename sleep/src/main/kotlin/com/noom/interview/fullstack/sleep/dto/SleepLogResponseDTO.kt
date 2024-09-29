package com.noom.interview.fullstack.sleep.dto

data class SleepLogResponseDTO (
    val sleepDate: String?,
    val timeBedStart: String?,
    val timeBedEnd: String?,
    val totalTimeInBed: String,
    val morningFeeling : String?,
)