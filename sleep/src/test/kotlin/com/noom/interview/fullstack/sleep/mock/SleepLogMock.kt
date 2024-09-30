package com.noom.interview.fullstack.sleep.mock

import com.noom.interview.fullstack.sleep.enums.MorningFeelingEnum
import com.noom.interview.fullstack.sleep.model.SleepLog
import com.noom.interview.fullstack.sleep.model.User
import java.time.LocalDate
import java.time.LocalTime

class SleepLogMock {
    companion object {
        fun getSleepLogList() : List<SleepLog> {
            val user = User()
            return listOf(
                SleepLog(
                    id = 1L,
                    user = user,
                    sleepDate = LocalDate.now().minusDays(2),
                    timeInBedStart = LocalTime.of(22, 0),
                    timeInBedEnd = LocalTime.of(6, 0),
                    totalTimeInBed = 480L,
                    morningFeeling = MorningFeelingEnum.GOOD
                ),
                SleepLog(
                    id = 2L,
                    user = user,
                    sleepDate = LocalDate.now().minusDays(1),
                    timeInBedStart = LocalTime.of(23, 0),
                    timeInBedEnd = LocalTime.of(7, 0),
                    totalTimeInBed = 480L,
                    morningFeeling = MorningFeelingEnum.OK
                )
            )
        }

        fun getSleepLog() : SleepLog{
            return SleepLog(
                1L,
                User(),
                LocalDate.now(),
                LocalTime.now().minusHours(8),
                LocalTime.now(),
                480,
                MorningFeelingEnum.GOOD

            )
        }
    }
}