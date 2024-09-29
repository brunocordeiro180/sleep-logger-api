package com.noom.interview.fullstack.sleep.dto

import com.noom.interview.fullstack.sleep.enum.MorningFeelingEnum
import java.time.LocalDateTime
import javax.validation.constraints.NotNull

data class SleepLogRequestDTO (

    @field:NotNull(message = "{sleep.timeBedStart.required}")
    val timeInBedStart: LocalDateTime?,

    @field:NotNull(message = "{sleep.timeBedEnd.required}")
    val timeInBedEnd: LocalDateTime?,

    @field:NotNull(message = "{sleep.morningFeeling.required}")
    val morningFeeling : MorningFeelingEnum?

)