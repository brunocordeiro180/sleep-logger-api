package com.noom.interview.fullstack.sleep.dto.response

import com.noom.interview.fullstack.sleep.model.SleepLog
import com.noom.interview.fullstack.sleep.utils.DateUtils

data class SleepLogResponseDTO (
    val sleepDate: String?,
    val timeBedStart: String?,
    val timeBedEnd: String?,
    val totalTimeInBed: String,
    val morningFeeling : String?,
) {
    companion object{
        fun fromSleepLog(sleepLog: SleepLog): SleepLogResponseDTO {
            return SleepLogResponseDTO(
                sleepDate = DateUtils.formatDateWithOrdinal(sleepLog.sleepDate),
                timeBedStart = DateUtils.formatTimeTo12HourPeriod(sleepLog.timeInBedStart),
                timeBedEnd = DateUtils.formatTimeTo12HourPeriod(sleepLog.timeInBedEnd),
                totalTimeInBed = DateUtils.formatTimePassed(sleepLog.totalTimeInBed),
                sleepLog.morningFeeling?.value
            )
        }
    }
}