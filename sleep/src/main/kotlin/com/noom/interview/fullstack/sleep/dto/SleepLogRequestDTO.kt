package com.noom.interview.fullstack.sleep.dto

import com.noom.interview.fullstack.sleep.enum.MorningFeelingEnum
import java.time.LocalTime
import javax.validation.constraints.NotNull

data class SleepLogRequestDTO (

    @field:NotNull(message = "{sleep.timeBedStart.required}")
    val timeInBedStart: LocalTime?,

    @field:NotNull(message = "{sleep.timeBedEnd.required}")
    val timeInBedEnd: LocalTime?,

    @field:NotNull(message = "{sleep.morningFeeling.required}")
    val morningFeeling : MorningFeelingEnum?

)